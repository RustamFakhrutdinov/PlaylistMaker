Android-приложение Playlist Maker
Приложение для прослушивания музыки и создания плейлистов.
Функции приложения:
- поиск музыкальных произведений на экране "Поиск": доступ к музыкальным произведениям получен через iTunes Search API, с помощью библиотеки Retrofit и Gson. Каждый трек представляет собой элемент списка RecyclerView. При нажатии на трек открывается экран со всей информацией произведения, на котором также можно прослушать отрывок из 30 секунд(реализовано с использованием класса MediaPlayer). Отображение обложки трека реализовано с помощью библиотеки Glide. Отслеживание прогресса воспроизведения трека и автоматизированное выполнение запроса поиска реализованы с помощью Coroutines. Также присутствует история поиска произведений.
- темная тема: на экране "Настройки" можно переключать темную тему, которая будет сохраняться даже после закрытия приложения(реализовано с помощью Shared Prefernces)
- для управления навигацией в приложении используется Jetpack Navigation Component(с помощью Bottom Navigation View можно переключаться между экранами)
- приложение использует подходы Clean Architecture(шаблон MVVM) и Single Activity(экраны реализованы с помощью Fragment)
- хранение избранных треков и плейлистов происходит в базе данных, которая реализована с помощью библиотеки Room. Работа с базой данных происходит с помощью корутин.
