# RxAudioController
Abstract layer that allows to play audio in background seamlessly

You can create MediaManager by calling one of two factory methods. 

MediaManagerFactory.getMediaManager() - creates simple MediaManager, that will be attached to lifecycle of context, so it will be destroyed after configuration change
MediaManagerFactory.getServiceMediaManager() - creates MediaManager, that will be attached to service.

All functionality are contained in interface MediaManager:

interface MediaManager {

    fun loadStreamMusic(url: String, attributes: AudioAttributes? = null)
    fun loadResourceMusic(resourceId: Int, attributes: AudioAttributes? = null)
    fun loadExternalFileMusic(filePath: String, attributes: AudioAttributes? = null)
    fun loadInternalFileMusic(filePath: String, attributes: AudioAttributes? = null)
    fun seekTo(millisecond: Millisecond)
    fun finish()
    fun pause()
    fun start()
    fun resume()
    fun stop()
    fun reset()
    fun getMediaInfoObservable(): Observable<MediaInfo>

}

Function getMediaInfoObservable provides observable, that you can subscribe to and get media info:

data class MediaInfo(val state: MediaState, val bufferProgress: Percent,
                     val currentTimeMilis: Millisecond,
                     val currentTime: Second,
                     val duration: Second)

Library uses some external classes that are not provided within, so you may have to add them in gradle:


    implementation "android.arch.lifecycle:extensions:$android_architecture_components_version"
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"


    api 'io.reactivex.rxjava2:rxkotlin:2.2.0'
    api 'io.reactivex.rxjava2:rxandroid:2.0.1'

    api 'com.tbruyelle.rxpermissions2:rxpermissions:0.9.5@aar' - for playing external file music. 
