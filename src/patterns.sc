patterns:
    $hello = (привет* [привет]|привеет: привет|приивеет: привет|приветик|приветики|при вед: привед|здравствуй|здравствуй*|здрасьте|здрасти|high: хэй|эй: хэй|хай|хэлло*|халоу|хэлоу|здаров|шалом|hello|здарова|хелло|здаровеньки|здоровеньки|здрасте|салют|hi|приветствую|~добрый ~день|~добрый ~вечер|доброго времени суток|хеллоу|п р и в е т|здраствуй*|приве: привет|прив: привет|хаюшки)
    $noWant = (нет|неа|ниат|no|не хочу|в другой раз|нет спасибо)
    $yesWant =  ([ну] конечно|всё|все|вроде|пожалуй|возможно|да|даа|lf|ага|точно|угу|верно|ок|ok|окей|окай|okay|именно|подтвержд*|йес|конечно|конешно|канешна|вроде|давай|хочу|почему [бы и] нет) 
    $somethingElseForWeather = (ещё один*|ещё один прогноз*|а* @duckling.date|* $City *|* {$City * * @duckling.date} *)
    $noQuestions = (вопросов нет|нет вопросов|у меня больше нет вопросов)
    $offerreject = (нет извините|извините нет|да зачем мне|да незачем|да мне зачем|да не интересует|ничего не желаю|не ничего|все не надо|все не надо|не не надо спасибо большое|уже не надо|не надо ничего|надо не надо|ой не надо спасибо|ничего мне не надо спасибо|девушка не надо спасибо|это не интересно|мне надо спасибо|не интересно это|мне спасибо|у меня уже есть|ничего все до свидания|да не|да не не надо спасибо|да мне не удобно|да не удобно мне|не стоит|мне не нужны деньги|да не просто как бы смысла нет|не интересно|не знаю|надо ничего|мне не нужна смс|да ну *|* не * (сбрось*/сбрасывай*/пошлите/посылай*/управляй/направь*/направляй*/треб*) *|* не * (скинь*/скидывай*/кинь*/кидай/проправь/поправляй*)|* не * (отправляй*/не отправлял*/отправ*) *|* да ну нафи* *|* пока что не *|* да нет *|* нет * [мне] не надо *|* нет *|* надо мне ничего отправлять *|* не нужен *|* [да] не надо мне *|* никакой кредит *|* не заказывал *|да мне не надо *|* [мне] (не нужно/не надо) [ничего] *|* [ничего] [мне] (не нужно/не надо) *|* (не нужно/не надо) * [ничего] [мне] *|* (спасибо/благодарю) нет *|* нет (спасибо/благодарю) *|* {спасибо не*} *|* [я] (откажусь/отказываюсь) *|* (откажусь/отказываюсь) [я] *|* никакую *|* не надо мне ничего отправлять|* отправлять мне ничего не нужно|* не надо мне ничего *|нет не не нужно|* я уже|он актуально|* уже я * *|* пожалуйста не* *|не* пожалуйста *|* зачем мне это надо * пожалуйста *|* не (хочу/хотел* бы/хотелось */хочется) *|* я не буду ничего *|* оно вам надо *|* мне не интересно *|* не надо спасибо да|* да вообще блин *|нет да не не не [не]|* не желаю [спасибо] *|* не хочу *|ничего не *|* не интересно|мне не (нужно/надо) [спасибо]|* пока не надо спасибо|* пока ничего|* не надо мне ничего *|девушка не интересно [спасибо]|уже не актуально *|не это не интересно * спасибо|* [спасибо] меня * не интересует|* пока не интересует|[нет] я не (согласен|согласна)|* смысла нет *|* нет смысла *|* не стоит это мне сейчас не интересует|* нет я пока не нуждаюсь|спасибо всё есть *|давайте вы [мне] ничего не будете отправлять|хорошо [мне] не надо [спасибо]|* давайте в следующий раз (какнибудь|не сейчас) *|* да я не буду *|* уже не* нужд* *)