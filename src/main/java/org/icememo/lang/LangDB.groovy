package org.icememo.lang

import org.apache.commons.io.IOUtils

class LangDB {
    public static LangDB instance

    static LangDB getInstance() {
        if (instance == null) {
            instance = new LangDB()
        }
        return instance
    }

    Language eng = new Language(code: "eng")
    Language rus = new Language(code: "rus")

    LangDB() {
        addTrans("menu_watch", "Watch", "Просмотр")
        addTrans("menu_about", "About", "О системе")
        addTrans("menu_add", "Add", "Добавить")
        addTrans("menu_repeat", "Repeat", "Повторить")
        addTrans("menu_stat", "Statistics", "Статистика")
        addTrans("menu_browser", "Browser", "Обзор")
        addTrans("menu_settings", "Settings", "Настройки")
        addTrans("menu_info", "Info", "Инфо")
        addTrans("menu_change_user", "Change user", "Сменить пользователя")
        addTrans("menu_login", "Login", "Вход")
        addTrans("menu_key_incorrect", "Key is incorrect!", "Неправильный ключ!")


        addTrans("main_demo_title", "Please, buy full version to use this function!", "Чтобы использовать эту функцию, нужно купить полную версию программы")

        addTrans("main_demo_body", "In demo version:\n" +
                "                <ol>\n" +
                "                    <li>There's no statistics</li>\n" +
                "                    <li>There's no clip browser</li>\n" +
                "                    <li>Clip limit - only 100 clips</li>\n" +
                "                </ol>\n" +
                "                <hr>\n" +
                "                Please, proceed to <a href=\"https://allsoft.ru/\">https://allsoft.ru/software/independent-vendors/4228316/icememo-izuchenie-yazykov-po-serialam/</a> to buy full version",
                "Ограничения демонстрационной версии:\n" +
                        "                <ol>\n" +
                        "                    <li>Нет подуля статистики</li>\n" +
                        "                    <li>Нет обозревателя</li>\n" +
                        "                    <li>Ограничение на количество клипов - 100 штук</li>\n" +
                        "                    <li>Ограничение на видео - 10 штук</li>\n" +
                        "                </ol>\n" +
                        "                <hr>\n" +
                        "                Пожалуйта, проследуйте на <a href=\"https://allsoft.ru/software/independent-vendors/4228316/icememo-izuchenie-yazykov-po-serialam/\" target='_blank'>https://allsoft.ru/software/independent-vendors/4228316/icememo-izuchenie-yazykov-po-serialam</a>, чтобы купить полную версию")



        addTransText("main_about_html", "about.html");
        addTrans("main_community", "Community", "Сообщество");
        addTrans("main_about", "About", "О программе");
        addTrans("main_activation", "Activation", "Активация");

        addTrans("add_tv_series", "TV Series", "Сериалы");
        addTrans("add_add", "Add...", "Добавить...");
        addTrans("add_add_title", "Add...", "Добавить");
        addTrans("add_edit", "Edit", "Редактировать");
        addTrans("add_delete", "Delete", "Удалить");
        addTrans("add_download_from_web", "Download from web", "Скачать из интернета");
        addTrans("add_from_ororo", "From ororo.tv", "С сайта ororo.tv");
        addTrans("add_seasons", "Seasons", "Сезоны");
        addTrans("add_please_select_series", "Please select TV series", "Пожалуйста, выберите сериал");
        addTrans("add_videos", "Videos", "Серии");
        addTrans("add_please_select_season", "Please select a season", "Пожалуйста, выберите сезон");
        addTrans("add_upload", "Upload...", "Загрузить...");
        addTrans("add_add_series", " Add TV Series", "Добавить сериал");
        addTrans("add_edit_series", "Edit TV series", "Редактировать сериал");
        addTrans("add_name", "Name", "Название");
        addTrans("add_volume", "Volume", "Громкость");
        addTrans("add_add_season", "Add Season", "Добавить сезон");
        addTrans("add_edit_season", "Edit season", "Редактировать сезон");
        addTrans("add_ororo_header", "Download from <a href=\"http://ororo.tv\" target='_blank'>http://ororo.tv</a>", "Загрузка с <a href=\"http://ororo.tv\" target='_blank'>http://ororo.tv</a>");
        addTrans("add_ororo_login", "Email", "Почтовый ящик");
        addTrans("add_ororo_password", "Password", "Пароль");
        addTrans("add_ororo_optional", " (optional)", " (необязательно)");
        addTrans("add_ororo_find", "Find!", "Поиск!");
        addTrans("add_ororo_change_season", "Change season", "Выбрать другой сериал и сезон");
        addTrans("add_ororo_start_download", "Start download...", "Начать загрузку...");
        addTrans("add_ororo_progress", "Progress", "Прогресс");
        addTrans("add_ororo_video", "Video", "Видео");
        addTrans("add_ororo_wait", "Wait", "Ожидание");
        addTrans("add_ororo_added", "OK", "Успешно загружено");
        addTrans("add_upload_to", "Upload videos to", "Добавление серий к ");
        addTrans("add_add_formats", "Add (*.webm/*.mp4)", "Загрузить (*.webm/*.mp4)");
        addTrans("add_add_convert", "Convert (*.mkv, *.avi, etc)", "Преобразовать (*.mkv, *.avi, etc)");
        addTrans("select_webm_hint", "Select videos and subtitles for them (Example - <b>video1.webm</b> and <b>video1.srt</b>)",
                "Выберите видео и субтитры для них (Пример - <b>video1.webm</b> и <b>video1.srt</b>)");
        addTransText("add_convert_html", "convert.html");

        addTransText("hdd_hint", "hdd_hint.html");

        addTrans("add_table_size", "Size (MB)", "Размер (MB)");
        addTrans("add_table_subtitles", "Subtitles", "Субтитры");
        addTrans("add_actions", "Actions", "Действия");
        addTrans("add_please_enter_name", "Please enter name!", "Пожалуйста, введите имя!");
        addTrans("add_tv_series_contain_seasons", "You can't delete TV series that contain some seasons. Please delete seasons and their content first", "Вы не можете удалить сериал, который содержит сезоны. Пожалуйста, удалите эти сезоны и их содержимое");
        addTrans("add_del_season_error", "You can't delete season that contain some videos. Please delete videos first", "Вы не можете удалить сезон, который содержит видео. Пожалуйста, сначала удалите видео");
        addTrans("add_del_clips_error", "You can't delete this video because some clips use it. At first, you need to delete this clips", "Вы не можете удалить это видео, потому, что некоторые клипы созданы на его основе. Пожалуйста, сначала удалите эти клипы");
        addTrans("add_ready_to_upload", "Ready to upload...", "Готов с загрузке...");
        addTrans("add_sure_del_from_queue", "Are you sure you want to delete this video from queue?", "Вы уверены, что хотите удалить это видео из очереди на загрузку?");
        addTrans("add_status_uploading", "Uploading...", "Загрузка...");
        addTrans("add_open_watch", "Watch videos", "Посмотреть загруженные серии");

        addTrans("add_please_reg", "Please, register on <a href=\"http://ororo.tv\" target='_blank'>http://ororo.tv</a> (it's free)", "Пожалуйста, зарегистрируйтесь на <a href=\"http://ororo.tv\" target='_blank'>http://ororo.tv</a> и введите ваш логин и пароль, чтобы скачивать видео (это бесплатно)");

        //Browser
        addTrans("bro_restrictions", "Restrictions", "Ограничения");
        addTrans("bro_suspended", "Suspended", "Исключённые");
        addTrans("bro_marked", "Marked", "Отмеченные");
        addTrans("bro_bro", "Browser", "Обзор");
        addTrans("bro_bro_clips", "Browser", "Обзор клипов");
        addTrans("bro_search", "Search", "Поиск");
        addTrans("bro_created", "Created", "Создан");
        addTrans("bro_text", "Text", "Текст");
        addTrans("bro_last_date", "Last Date", "Последнее повторение");
        addTrans("bro_next_date", "Next Date", "Следующее повторение");
        addTrans("bro_left_days", "Left Days", "Осталось дней до повторения");
        addTrans("bro_commands", "Commands", "Команды");
        addTrans("bro_open", "Open", "Открыть");
        addTrans("bro_clip_details", "Clip details", "Обзор клипа");
        addTrans("bro_delete", "Delete", "Удалить");
        addTrans("bro_close", "Close", "Закрыть");
        addTrans("bro_sure_del_clip", "Are you sure you want to delete this clip?", "Вы уверены, что хотите удалить этот клип?");

        addTrans("info_info", "Info", "Главное меню");
        addTrans("info_gen", "General", "Общая информация");
        addTrans("info_lics", "Licences", "Лицензии");
        addTrans("info_version", "Version", "Версия");
        addTrans("info_codename", "codename", "кодовое имя");
        addTrans("info_phoebe", "Phoebe", "Фиби");
        addTrans("info_build_date", "Build date", "Дата компиляции");
        addTrans("info_spring_version", "Spring version", "Версия Spring");
        addTrans("info_hibernate_version", "Hibernate version", "Версия Hibernate");
        addTrans("info_main_folder", "Main folder", "Главная папка");
        addTrans("info_app_folder", "Application folder", "Папка с приложением");

        addTrans("login_users", "Users", "Пользователи");
        addTrans("login_create", "Create", "Создать");

        addTrans("repeat_new", "New", "Новые");
        addTrans("repeat_learn", "Learn", "Разбор");
        addTrans("repeat_release", "Release", "Выпуск");
        addTrans("repeat_repeat", "Repeat", "Повторение");

        addTrans("repeat_cmd_rep", "Replay", "Повторить");
        addTrans("repeat_cmd_slow", "Speed 50%", "Скорость 50%");
        addTrans("repeat_cmd_slow_80", "Speed 80%", "Скорость 80%");

        addTrans("repeat_cmd_pause", "Pause", "Пауза");
        addTrans("repeat_cmd_subtitles", "Subtitles", "Субтитры");
        addTrans("repeat_cmd_suspend", "Suspend", "Исключить");

        addTrans("repeat_repeat", "Repeat", "Повторение");
        addTrans("repeat_now", "Now", "Сейчас");
        addTrans("repeat_sec", "sec", "сек");
        addTrans("repeat_min", "min", "мин");
        addTrans("repeat_good", "Good", "Норма");
        addTrans("repeat_reset", "Reset", "Сбросить");
        addTrans("repeat_later", "Later", "Потом");
        addTrans("repeat_hard", "Hard", "Сложно");
        addTrans("repeat_easy", "Easy", "Легко");
        addTrans("repeat_all", "All", "Всё");
        addTrans("repeat_results", "Results", "Результаты повторения");
        addTrans("repeat_show_stat", "Show repetition statistics", "Показать статистику повторений");
        addTrans("repeat_stat", "Repetition statistics", "Статистика повторений");
        addTrans("repeat_suspended", "Suspended", "Исключено");
        addTrans("repeat_fc_review", "Flashcards review", "Обзор карточек");

        addTrans("settings_settings", "Settings", "Настройки");
        addTrans("settings_general", "General", "Общие");
        addTrans("settings_language", "Language", "Язык");

        addTrans("stat_stat", "Statistics", "Статистика");
        addTrans("stat_videos", "Videos", "По сериям");
        addTrans("stat_seasons", "Seasons", "По сезонам");
        addTrans("stat_select_series", "-Select TV Series-", "[Выберите сериал]");
        addTrans("stat_select_season", "-Select Season-", "[Выберите сезон]");
        addTrans("stat_name", "Name", "Имя");
        addTrans("stat_clips", "Clips", "Клипы");
        addTrans("stat_fc", "FlashCards", "Карточки");
        addTrans("stat_clips_avg", "Clips AVG", "Клипы (среднее)");
        addTrans("stat_fc_avg", "FC AVG", "Карточки (среднее)");
        addTrans("stat_video", "Videos", "Видео");
        addTrans("stat_done", "Done", "Прогресс");

        addTrans("watch_cut", "Cut clip", "Вырезать клип");
        addTrans("watch_clips", "Clips", "Клипы");
        addTrans("watch_update_browser", "Please, update browser", "Пожалуйста, обновите браузер");
        addTransText("watch_update_browser_html", "watch_update_browser.html")

        addTrans("watch_subtitle_stack", "Subtitle stack", "Субтитры");
        addTrans("watch_add_new_clip", "Add new clip", "Добавить новый клип");
        addTrans("watch_begin", "Begin", "Начало");
        addTrans("watch_end", "End", "Конец");
        addTrans("watch_subtitles_shift", "Subtitles", "Субтитры (сдвиг)");
        addTrans("watch_add_clip", "Add clip", "Добавить клип");
        addTrans("watch_add_fc", "Add flashcard", "Добавить карточку");
        addTrans("watch_subtitles", "Subtitles", "Субтитры");
        addTrans("watch_transc", "Transcripts", "Транскрипция");
        addTrans("watch_upload", "Upload", "Загрузка");
        addTrans("watch_fc", "Flashcards", "Карточки");
        addTrans("watch_select_transc", " Select transcripts file (html)", "Выбрать файл транскрипции (html)");
        addTrans("watch_help_subtle", "Default step - 0.2 ms <br>Hold key <b>Ctrl</b> for subtle step (0.1)",
                "Шаг по умолчанию - 0.2 мс <br>Удерживайте клавишу <b>Ctrl</b> для меньшего шага (0.1 мс)");
        addTrans("watch_help_show_subtitles", "Show subtitle - hotkey <b>arrow down</b>",
                "Показать субтитры - горячая клавиша <b>стрелка вниз</b>");
        addTrans("watch_make_pause_on_end", "Make pause on subtitle end", "Делать паузу на конце фраз");


        addTrans("d_first_launch", "First launch!", "Первый запуск!");
        addTrans("d_first_launch_text", "IceMemo was successfully installed", "IceMemo был успешно установлен");
        addTrans("d_path", "Path", "Путь");
        addTrans("d_or_you_can_type", "Or you can type", "Также, вы можете ввести адрес");
        addTrans("d_in_browser", "in browser", "в браузере");
        addTrans("d_cp", "IceMemo control panel", "Панель управления IceMemo");
        addTrans("d_demo", "Demo version", "Демонстрационная версия");
        addTrans("d_full", "Full version", "Полная версия");

        addTrans("d_stop_server", "Stop server", "Остановить сервер");
        addTrans("d_sure_stop_exit", "Are you sure you want to stop server and close program?", "Вы уверены что хотите остановить сервер и закрыть программу?");

        addTrans("d_change", "Change", "Выбрать");
        addTrans("d_main_folder", "Main folder", "Главная папка");
        addTrans("d_please_select_main_folder", "Please, select main folder", "Пожалуйста, выберите главную папку");
        addTrans("d_info", "Info", "Информация");
        addTrans("d_start", "Start", "Запуск");
        addTrans("d_stop", "Stop", "Остановить");
        addTrans("d_open", "Open", "Открыть");
        addTrans("d_port", "Port", "Порт");


        addTrans("d_server", "Server", "Сервер");
        addTrans("d_choose_main_folder","Choose main folder", "Выберите главную папку");
        addTrans("d_create_new_db","Create new database?", "Создать новую базу данных?");
        addTrans("d_create_new_db_sure","Would you like to create new database in this folder?", "Создать новую базу данных в этой папке?");
        addTrans("d_input_name", "Input name", "Введите имя");
        addTrans("d_name", "Name", "Имя");
        addTrans("d_error", "Error", "Ошибка");
        addTrans("d_cannot_open", "Program can't open selected folder!", "Программа не может открыть выбранную папку!");
        addTrans("d_error_select", "Please, select empty folder (if you want to create new database), or \" +\n" +
                "                    \"select folder with existing dababase", "Пожалуйста, выберете пустую папку (если вы хотите создать новую базу данных), или выберете папку с уже созданой базой");
        addTrans("d_creation_date", "Creation date", "Дата создания");
        addTrans("d_db_selected", "Base selected!", "База выбрана!");
        addTrans("d_already_in_use", "already_in_use", "уже используется другой программой");
        addTrans("d_select_another_port", "Please, input another port", "Пожалуйста, выберите другой порт");

        addTrans("d_port_error", "You can't use this port", "Использование выбранного вами порта запрещено");


        addTrans("w_today_plan", "Plan for today", "План на сегодня");
        addTrans("w_repeat_old", "Repeat old clips", "Повторить старые клипы");
        addTrans("w_repeat_new", "Learn new clips", "Разобрать новые клипы ");
        addTrans("w_create_new_clips", "Create 20-30 new clips", "Посмотреть серию и вырезать 20-30 новых клипов");


        addTrans("c_empty", "[Empty]", "[Пусто]");//{{::l.c_empty}}
        addTrans("c_ok", "Ok", "Хорошо");//{{::l.c_ok}}
        addTrans("c_yes", "Yes", "Да");//{{::l.c_yes}}
        addTrans("c_no", "No", "Нет");//{{::l.c_no}}
        addTrans("c_sure", "Are you sure?", "Вы уверены?");//$scope.l.c_sure

        addTrans("c_select_all", "Select all", "Выбрать всё");
        addTrans("c_deselect_all", "Deselect all", "Снять выбор");

        /*for(int i=1;i<1000;i++){
            addTrans("c_${DateUtils.uniqueFileName}_$i", "Yes${DateUtils.uniqueFileName}", "Да${DateUtils.uniqueFileName}");
        }*/
    }

    void addTransText(String key, String file){
        addTransTextForLang(key, file, eng)
        addTransTextForLang(key, file, rus)
    }

    void addTransTextForLang(String key, String file, Language lang){
        String propFileName = "/config/lang/${lang.code}/$file";
        println(propFileName)
        InputStream inputStream = getClass().getResourceAsStream(propFileName)
        String content = IOUtils.toString((InputStream)inputStream, "UTF-8");

        LangEl langEl = new LangEl(key: key, value: content)
        lang.list << langEl
    }

    void addTrans(String key, String engStr, String rusStr){
        LangEl langElEng = new LangEl(key: key, value: engStr)
        eng.list << langElEng

        LangEl langElRus = new LangEl(key: key, value: rusStr)
        rus.list << langElRus

    }
}
