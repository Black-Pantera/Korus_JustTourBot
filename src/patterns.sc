patterns:
    $hello = (привет* [привет]|привеет: привет|приивеет: привет|приветик|приветики|при вед: привед|здравствуй|здравствуй*|здрасьте|здрасти|high: хэй|эй: хэй|хай|хэлло*|халоу|хэлоу|здаров|шалом|hello|здарова|хелло|здаровеньки|здоровеньки|здрасте|салют|hi|приветствую|~добрый ~день|~добрый ~вечер|доброго времени суток|хеллоу|п р и в е т|здраствуй*|приве: привет|прив: привет|хаюшки)
    $noWant = (нет|неа|no|не хочу)
    $yesWant = (да|ага|yes|ога|хочу)
    $somethingElseForWeather = (* {$City * * @duckling.date} *|* а в городе $City *)