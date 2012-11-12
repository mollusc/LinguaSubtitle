#LinguaSubtitle
Программа предназначена для генерации субтитров на основе вашего словарного запаса

##Требования
* Java 1.6 и более поздние версии.
* VLC плеер

##Установка
1. Скачать архив [https://github.com/mollusc/LinguaSubtitle/downloads](https://github.com/mollusc/LinguaSubtitle/downloads).
2. Распаковать скаченный архив и запустить файл **LinguaSubtitle.jar**.

> **Замечание**: В распакованной папке находится файл **Vocabulary.db**, который уже содержит перевод около 7500 наиболее часто встречающихся слов в фильмах. Если вы не хотите использовать данный словарь, то просто удалите файл **Vocabulary.db**.

##Использование
Для начала работы с программой необходимо загрузить субтитры, используя кнопку **Загрузка субтитров**. Далее выбрать файл субтитров .srt. После загрузки и обработки файла таблица слов будет иметь следующий вид:

![Таблица][1]

* Первая колонка указывает, известно ли слово или нет.
* Во второй колонке находятся слова, найденные в субтитрах.
* В третьей колонке перевод. Если ячейка перевода пуста, то считается, что пользователь знает значение этого слова и хочет изучить его применение в контексте, в этом случае слово будет просто выделено в субтитрах.
* Четвертая колонка отображает, сколько раз данное слово встречается в загруженном субтитре.
* В пятой колонке количество субтитров, в которых встречалось это слово.

После того как просмотрены все слова и внесены соответствующие изменения переходим к блоку **Экспорт**:

![Экспорт][2]

* **Скрывать известные диалоги** - скрыть диалог, в котором все слова отмечены как известные.
* **Известные слова** - выбор цвета для слов помеченных как известные.
* **Перевод** - выбор цвета для перевода.
* **Изучаемые слова** - выбор цвета для слов, которые неизвестны пользователю и имеют перевод.
* **Знакомые слова** - выбор цвета для слов, которые не имеют перевода.
* **Генерация субтитров** - генерация субтитров, а так же сохранение данных таблицы в словарь **Vocabulary.db**.
* **Сохранить данные** - сохранить данные таблицы в  словарь **Vocabulary.db** без формирования субтитров.

**ВАЖНО! В настройках плеера в качестве шрифта для субтитров выберите любой из моноширинныx шрифтов с поддержкой кириллицы например: Consolas, Courier New и др.**

В итоге должны получиться примерно такие субтитры:

![Пример][3]

[1]: https://raw.github.com/mollusc/LinguaSubtitle/master/screenshots/Table.png
[2]: https://raw.github.com/mollusc/LinguaSubtitle/master/screenshots/Export.png
[3]: https://raw.github.com/mollusc/LinguaSubtitle/master/screenshots/Examle.png