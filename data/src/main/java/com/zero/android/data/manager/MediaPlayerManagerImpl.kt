package com.zero.android.data.manager

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import com.zero.android.common.extensions.nullableBlocking
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import linc.com.amplituda.Amplituda
import linc.com.amplituda.Cache
import linc.com.amplituda.callback.AmplitudaErrorListener
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class MediaPlayerManagerImpl
@Inject
constructor(@ApplicationContext private val context: Context) : MediaPlayerManager {
	override val baseFilePath by lazy { "${context.externalCacheDir?.absolutePath ?: ""}/Memos" }

	private val amplituda: Amplituda by lazy { Amplituda(context) }

	init {
		File(baseFilePath).apply {
			if (!this.exists()) {
				this.mkdirs()
			}
		}
	}

	private var _recorder: MediaRecorder? = null
	override var recorderFilePath: String? = null

	override val mediaPlayer =
		MediaPlayer().apply {
			setAudioAttributes(
				AudioAttributes.Builder()
					.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
					.setUsage(AudioAttributes.USAGE_MEDIA)
					.build()
			)
		}

	@Throws(IOException::class)
	override fun startRecording() {
		recorderFilePath = "$baseFilePath/Memo-${System.currentTimeMillis()}"
		recorderFilePath?.let {
			_recorder =
				MediaRecorder().apply {
					setAudioSource(MediaRecorder.AudioSource.MIC)
					setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
					setOutputFile(it)
					setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
					setAudioEncodingBitRate(16 * 44100)
					setAudioSamplingRate(44100)

					prepare()
					start()
				}
		}
	}

	@Throws(IOException::class)
	override fun stopRecording() {
		_recorder?.apply {
			stop()
			reset()
			release()
		}
		_recorder = null
	}

	override fun prepareMediaPlayer(uri: Uri, onPlayBackComplete: () -> Unit) {
		mediaPlayer.apply {
			try {
				stop()
				reset()
				setDataSource(context, uri)
				setOnCompletionListener { onPlayBackComplete.invoke() }
				prepare()
			} catch (e: IOException) {}
		}
	}

	override fun getFileDuration(file: File): String? {
		return nullableBlocking {
			val retriever = MediaMetadataRetriever()
			retriever.setDataSource(context, Uri.fromFile(file))
			val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
			retriever.release()
			time
		}
	}

	override suspend fun getAudioAmplitudes(file: File): List<Int> =
		withContext(Dispatchers.IO) {
			return@withContext amplituda
				.processAudio(file.absolutePath, Cache.withParams(Cache.REUSE))
				.get(AmplitudaErrorListener { it.printStackTrace() })
				.amplitudesAsList()
				.dropWhile { it == 0 }
		}
}
