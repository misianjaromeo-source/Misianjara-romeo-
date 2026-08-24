package com.example.sound

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

object SoundManager {
    private val scope = CoroutineScope(Dispatchers.Default)
    private const val SAMPLE_RATE = 22050
    var isMuted: Boolean = false
    var soundVolume: Float = 1.0f

    enum class SfxType {
        WHISTLE,
        DOUBLE_WHISTLE,
        LONG_WHISTLE,
        KICK_PASS,
        KICK_POWER_SHOT,
        GOAL_ROAR,
        POST_HIT,
        TACKLE,
        UI_CLICK,
        GOLD_REWARD
    }

    fun play(sfx: SfxType) {
        if (isMuted) return
        scope.launch {
            try {
                when (sfx) {
                    SfxType.WHISTLE -> generateTone(frequency = 2800.0, durationMs = 350, modulation = 40.0)
                    SfxType.DOUBLE_WHISTLE -> {
                        generateTone(frequency = 2800.0, durationMs = 180, modulation = 40.0)
                        Thread.sleep(80)
                        generateTone(frequency = 2950.0, durationMs = 300, modulation = 45.0)
                    }
                    SfxType.LONG_WHISTLE -> generateTone(frequency = 2750.0, durationMs = 800, modulation = 35.0)
                    SfxType.KICK_PASS -> generateThud(frequency = 120.0, durationMs = 90, punch = 1.2f)
                    SfxType.KICK_POWER_SHOT -> generateThud(frequency = 85.0, durationMs = 180, punch = 2.2f)
                    SfxType.GOAL_ROAR -> generateCrowdRoar(durationMs = 1200)
                    SfxType.POST_HIT -> generateMetallic(frequency = 1400.0, durationMs = 300)
                    SfxType.TACKLE -> generateSlideNoise(durationMs = 220)
                    SfxType.UI_CLICK -> generateTone(frequency = 880.0, durationMs = 40, modulation = 0.0)
                    SfxType.GOLD_REWARD -> {
                        generateTone(frequency = 784.0, durationMs = 70, modulation = 0.0)
                        Thread.sleep(50)
                        generateTone(frequency = 1046.5, durationMs = 120, modulation = 0.0)
                    }
                }
            } catch (e: Exception) {
                // Ignore audio play exceptions gracefully
            }
        }
    }

    private fun generateTone(frequency: Double, durationMs: Int, modulation: Double) {
        val numSamples = (SAMPLE_RATE * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val modFreq = frequency + sin(2 * PI * 25.0 * t) * modulation
            val envelope = when {
                i < numSamples * 0.1 -> i / (numSamples * 0.1)
                i > numSamples * 0.8 -> (numSamples - i) / (numSamples * 0.2)
                else -> 1.0
            }
            val sample = (sin(2 * PI * modFreq * t) * Short.MAX_VALUE * 0.45 * envelope * soundVolume).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playRawBuffer(buffer)
    }

    private fun generateThud(frequency: Double, durationMs: Int, punch: Float) {
        val numSamples = (SAMPLE_RATE * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val pitchDecay = frequency * (1.0 - (i.toDouble() / numSamples) * 0.7)
            val envelope = (1.0 - (i.toDouble() / numSamples)) * (1.0 - (i.toDouble() / numSamples))
            val sinVal = sin(2 * PI * pitchDecay * t)
            val noise = (Random.nextFloat() - 0.5f) * 0.15f
            val sample = ((sinVal + noise) * Short.MAX_VALUE * 0.6f * punch * envelope * soundVolume).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playRawBuffer(buffer)
    }

    private fun generateCrowdRoar(durationMs: Int) {
        val numSamples = (SAMPLE_RATE * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        var lowPass = 0.0
        for (i in 0 until numSamples) {
            val rawNoise = (Random.nextDouble() - 0.5) * 2.0
            lowPass += 0.08 * (rawNoise - lowPass)
            val envelope = when {
                i < numSamples * 0.25 -> i / (numSamples * 0.25)
                i > numSamples * 0.6 -> (numSamples - i) / (numSamples * 0.4)
                else -> 1.0
            }
            val hornTone = sin(2 * PI * 340.0 * (i.toDouble() / SAMPLE_RATE)) * 0.25
            val sample = ((lowPass * 0.75 + hornTone) * Short.MAX_VALUE * 0.55 * envelope * soundVolume).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playRawBuffer(buffer)
    }

    private fun generateMetallic(frequency: Double, durationMs: Int) {
        val numSamples = (SAMPLE_RATE * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val envelope = (1.0 - (i.toDouble() / numSamples))
            val harmonic1 = sin(2 * PI * frequency * t) * 0.6
            val harmonic2 = sin(2 * PI * (frequency * 1.58) * t) * 0.3
            val harmonic3 = sin(2 * PI * (frequency * 2.3) * t) * 0.1
            val sample = ((harmonic1 + harmonic2 + harmonic3) * Short.MAX_VALUE * 0.6 * envelope * soundVolume).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playRawBuffer(buffer)
    }

    private fun generateSlideNoise(durationMs: Int) {
        val numSamples = (SAMPLE_RATE * durationMs) / 1000
        val buffer = ShortArray(numSamples)
        var filtered = 0.0
        for (i in 0 until numSamples) {
            val raw = (Random.nextDouble() - 0.5) * 2.0
            filtered += 0.25 * (raw - filtered)
            val envelope = (1.0 - (i.toDouble() / numSamples))
            val sample = (filtered * Short.MAX_VALUE * 0.5 * envelope * soundVolume).toInt()
            buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        playRawBuffer(buffer)
    }

    private fun playRawBuffer(buffer: ShortArray) {
        try {
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()
            scope.launch {
                Thread.sleep((buffer.size * 1000L / SAMPLE_RATE) + 100L)
                track.release()
            }
        } catch (e: Exception) {
            // AudioTrack init failure safeguard
        }
    }
}
