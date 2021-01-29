# Bansal Cinema

## Overview
Project ini merupakan salah satu syarat untuk memenuhi tugas 
ujian akhir semester dikampus saya. Oleh sebab itu, tidak ada
monetisasi apapun didalam poject ini.

Salah satu fitur yang saya disajikan yaitu, menampilkan data dinamis
dari public API. Data tersebut, merupakan kumpulan film yang disediakan
oleh [themoviedb.org](https://www.themoviedb.org/)

Jika anda ingin menggunakan API tersebut, silahkan baca dokumentasi
dari situs resmi nya [themoviedb.org/documentation/api](https://www.themoviedb.org/documentation/api)

## API Configuration

Cari file `gradle.properties` di `.gradle` pada home directory project anda.

Kemudian tambahkan `PopularMoviesApp_ApiKey = "YOUR-API-KEY"` di file tersebut.

Refrensi: [Hiding API keys from your Android repository](https://varunbarad.com/blog/hiding-api-keys-from-your-android-repository)

## Libraries
- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture/) 
    * [Paging](https://developer.android.com/topic/libraries/architecture/paging/) 
    * [Room](https://developer.android.com/topic/libraries/architecture/room)
    * [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
    * [LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
- [Android Data Binding](https://developer.android.com/topic/libraries/data-binding/)
- [Retrofit](http://square.github.io/retrofit/) for REST api communication
- [Picasso](http://square.github.io/picasso/) for image loading

## Screenshots
![popular-movies-main](https://user-images.githubusercontent.com/33213229/49940286-757e9100-ff22-11e8-897a-45ba561df250.png)![popular-movies-detail](https://user-images.githubusercontent.com/33213229/49940281-71527380-ff22-11e8-935b-7e2d4138d979.png)![popular-movies-info](https://user-images.githubusercontent.com/33213229/49940285-73b4cd80-ff22-11e8-9ca1-b379e06b90bc.png)
![popular-movies-trailers](https://user-images.githubusercontent.com/33213229/49940290-77485480-ff22-11e8-8ed5-e79430807c66.png)![popular-movies-cast](https://user-images.githubusercontent.com/33213229/49940272-6c8dbf80-ff22-11e8-9ea9-c873be4cd699.png)
