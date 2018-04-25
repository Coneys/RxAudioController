package github.com.coneey.rxaudiomanager.mediaListener

import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import java.util.*
import java.util.concurrent.TimeUnit

typealias MediaRunnable = (MediaPlayer) -> Unit

class MediaStateResolver(private val player: MediaPlayer) : MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener,
        AudioManager.OnAudioFocusChangeListener {

    private var refreshIntervalDisposable: Disposable? = null

    private val onPreparedRunnables: ArrayList<MediaRunnable> = ArrayList()
    private val stateSubject: Subject<MediaState> = BehaviorSubject.create()
    private val bufferSubject: Subject<Percent> = BehaviorSubject.create()
    private val positionSubject: Subject<Second> = BehaviorSubject.create()

    private val infoFunction3 = Function3 { t1: MediaState, t2: Percent, t3: Second -> MediaInfo(t1, t2, t3) }
    val infoSubject: Observable<MediaInfo> = Observable.combineLatest(stateSubject.startWith(MediaState.STOPPED), bufferSubject.startWith(100), positionSubject.startWith(0), infoFunction3)

    fun initialize(): Observable<MediaInfo> {
        player.let {
            it.setOnPreparedListener(this)
            it.setOnErrorListener(this)
            it.setOnSeekCompleteListener(this)
            it.setOnInfoListener(this)
            it.setOnBufferingUpdateListener(this)
        }

        return infoSubject
    }

    fun finalize() {
        refreshIntervalDisposable?.dispose()
        onPreparedRunnables.clear()
        player.release()
    }


    override fun onPrepared(mp: MediaPlayer) {
        startPlayer(mp)
        onPreparedRunnables.forEach { it.invoke(mp) }
        onPreparedRunnables.clear()
    }


    override fun onError(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        when (what) {
            MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK -> Log.d("MediaPlayer Error", "MEDIA ERROR NOT VALID FOR PROGRESSIVE PLAYBACK $extra")
            MediaPlayer.MEDIA_ERROR_SERVER_DIED -> Log.d("MediaPlayer Error", "MEDIA ERROR SERVER DIED $extra")
            MediaPlayer.MEDIA_ERROR_UNKNOWN -> Log.d("MediaPlayer Error", "MEDIA ERROR UNKNOWN $extra")
        }
        return false
    }

    override fun onSeekComplete(mp: MediaPlayer) {
        // println("COMPLETED SEEK")
    }

    override fun onInfo(mp: MediaPlayer, what: Int, extra: Int): Boolean {
        // println("INFO!!!")
        return false
    }

    override fun onBufferingUpdate(mp: MediaPlayer, percent: Int) {
        bufferSubject.onNext(percent)
        //  println("Buff update")
    }

    override fun onAudioFocusChange(focusChange: Int) {
        // println("Focus change")
    }

    fun postWhenPrepared(runnable: MediaRunnable) {
        onPreparedRunnables.add(runnable)
    }

    internal fun startPlayer(mp: MediaPlayer) {
        mp.start()
        stateSubject.onNext(MediaState.RUNNING)
        startRefreshInterval(mp)
    }


    private fun startRefreshInterval(mp: MediaPlayer) {
        val stateAndTimeBiFUnction = BiFunction { t1: Long, t2: MediaState ->
            t1 to t2
        }

        refreshIntervalDisposable?.dispose()
        refreshIntervalDisposable = Observable
                .combineLatest(Observable.interval(1, TimeUnit.SECONDS, Schedulers.newThread()), stateSubject, stateAndTimeBiFUnction)
                .filter { it.second == MediaState.RUNNING }
                .subscribeBy(onNext = { positionSubject.onNext(mp.currentPosition) })
    }

    fun pause() {
        if (player.isPlaying) {
            player.pause()
            stateSubject.onNext(MediaState.PAUSED)
        }
    }

    fun resume() {
        if (!player.isPlaying) {
            positionSubject.take(1)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        player.seekTo(it)
                        startPlayer(player)
                    }
        }
    }

    fun stop() {
        if (player.isPlaying) {
            println("STOPPING")
            player.pause()
            positionSubject.onNext(0)
            stateSubject.onNext(MediaState.STOPPED)
        }
    }


}