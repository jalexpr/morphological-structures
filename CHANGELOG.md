1.4.0
--
част. снятие омонимии на морф. анализе; испр. нач.формы глагола; подд. е вместо ё; расширение словаря

1.2.16
--

- Рефакторинг код 1.2.15 --

- Исправлена ошибка получения литеральной формы слова

1.2.14
--

- Поднята версия TemplateWrapperClasses

1.2.13
--

- Добавлен stream для RefOmoFormList

1.2.11
--

- Добавлено TawtException и TawtRuntimeException
- Добавлена часть речи UNFAMILIAR = 0x1
- Добавлен класс TypeForms
- Добавлен класс UnfamiliarOmoForm
- Добавлен в GetCharacteristics метод getTypeForm

1.2.10
--

- Добавлен метод, позволяющий определить omoForm является формой слова или числом

1.2.9
--

- нераспознанные слова отфильтровываются после gama

1.2.7
-----------------------------

- Смена лицензии
- Добавлены классы для экспорта результата

1.2.5
------------------------------

- перевезд на mvn
- добавил логгирование
- добавил лицензию (Attribution-NonCommercial-ShareAlike 3.0.pdf)
- заменил название пакета
- версия задается через tawt-parent
- вынес общую toString для *Form в Form
- добавил/улучшил toString