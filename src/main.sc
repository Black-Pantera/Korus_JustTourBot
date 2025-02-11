require: slotfilling/slotFilling.sc
  module = sys.zb-common
require: city/city.sc
    module = sys.zb-common  
require: name/name.sc
    module = sys.zb-common
  
require: funcs.js
require: moment.js
require: moment-with-locales.js
  
theme: /
    
    init: 
        bind("onAnyError", function($context) {
            var answers = [
                "Извините, произошла техническая ошибка. Специалисты обязательно изучат её и возьмут в работу. Пожалуйста, напишите в чат позже.",
                "Простите, произошла ошибка в системе. Наши специалисты обязательно её исправят."
            ];
            var randomAnswer = answers[$reactions.random(answers.length)];
            $reactions.answer(randomAnswer);
            
            $reactions.buttons({ text: "В главное меню", transition: "/Start" })
        }); 
        
        bind("postProcess", function($context) {
            $context.session.lastState = $context.currentState;
            if (context.request.channelType === "telegram") {
                _.each(context.response.replies, function(reply) {
                    if (reply.type === "text") {
                        reply.markup = "markdown";
                    }
            }
            
        });

    state: Start
        q!: $regex</start>
        q!: старт
        image: https://media.istockphoto.com/id/511095951/ru/%D1%84%D0%BE%D1%82%D0%BE/%D0%BE%D0%BD-%D0%B7%D0%B4%D0%B5%D1%81%D1%8C-%D1%87%D1%82%D0%BE%D0%B1%D1%8B-%D0%BF%D0%BE%D0%BC%D0%BE%D1%87%D1%8C.jpg?s=2048x2048&w=is&k=20&c=86_eS2vtvuPqNIFl04rO9yg1N7bv9yQMpqIrM0SNOH4=
        script:
            $context.session = {};
            $context.client = {};
            $temp = {};
        
        if: $client.name
            random:
                a: {{ capitalize($client.name) }}, здравствуйте! Артур из Just Tour на связи. Рад снова видеть вас в чате!
                a: {{ capitalize($client.name) }}, приветствую! На связи Артур из Just Tour, лучшей в мире туристической компании. Рад снова пообщаться с вами!
        else
           random:
                a: Здравствуйте! Меня зовут Артур, бот-помощник компании Just Tour. Расскажу все о погоде в городах мира и помогу с оформлением заявки на подбор тура.
                a: Приветствую вас! Я Артур, работаю виртуальным ассистентом в Just Tour, лучшем туристическом агентстве. Проинформирую вас о погоде в разных городах и соберу все необходимые данные для запроса на подбор путевки.
        
        go!: /HowCanIHelpYou
            
    state: GlobalCatchAll || noContext = true
        event!: noMatch
        script:
            $session.stateGlobalCounter++
                
        if: $session.stateGlobalCounter < 3
            random: 
                a: Прошу прощения, не совсем вас понял. Попробуйте, пожалуйста, переформулировать ваш вопрос.
                a: Простите, не совсем понял. Что именно вас интересует?
                a: Простите, не получилось вас понять. Переформулируйте, пожалуйста.
                a: Не совсем понял вас. Пожалуйста, попробуйте задать вопрос по-другому.
        else:
            a: Кажется, этот вопрос не в моей компетенции. Но я постоянно учусь новому, и, надеюсь скоро научусь отвечать и на него.
                
            script: 
                $session.stateGlobalCounter = 0
                    
            go!: /SomethingElse
            
    state: HowCanIHelpYou
        random:
            a: Чем могу помочь?
            a: Что вас интересует?
            a: Подскажите, какой у вас вопрос?
        script:
            $session.stateCounterInARow = 0;
        buttons:
            "Узнать прогноз погоды" -> /WeatherForecast
            "Оформить заявку на подбор тура" -> /TravelRequest
                
        state: CatchCallbackButton
            event: telegramCallbackQuery
            script:
                $temp.goTo = $request.query
            go!: {{$temp.goTo}}
                
        state: LocalCatchAll || noContex = true
            event: noMatch
            
            script:
                $session.stateCounterInARow ++;
                
            if: $session.stateCounterInARow < 3
                random: 
                    a: Извините, не совсем понял. Пожалуйста, подскажите, могу ли я чем-то вам помочь?
                    a: К сожалению, не смог понять, что вы имеете в виду. Подскажите, что вас интересует?
            else:
                script: 
                    $session.stateCounterInARow = 0;
                a: Кажется, этот вопрос не в моей компетенции. Но я постоянно учусь новому, и, надеюсь скоро научусь отвечать и на него.
                go!: /SomethingElse
           
    state: WeatherForecast
        intent!: /weather
        q!: * {$City * * @duckling.date} *
        script:
            if (($parseTree._City) && ($parseTree["_duckling.date"])) {
                $session.userCity = $parseTree._City.name;
                $session.lon = $parseTree._City.lon;
                $session.lat = $parseTree._City.lat;
                $session.country = $parseTree._City.country;   
                $session.userDate = new Date($parseTree["_duckling.date"].year + "/"+ $parseTree["_duckling.date"].month + "/"+ $parseTree["_duckling.date"].day);
                $reactions.transition("/CheсkDate");
                }
            else 
                if ($parseTree["_duckling.date"]) {
                    $session.userDate = new Date($parseTree["_duckling.date"].year + "/"+ $parseTree["_duckling.date"].month + "/"+ $parseTree["_duckling.date"].day);
                    $reactions.transition("/GetCity");
                    }
                else 
                    if ($parseTree._City) {
                        $session.userCity = $parseTree._City.name;
                        $session.lon = $parseTree._City.lon;
                        $session.lat = $parseTree._City.lat;
                        $session.country = $parseTree._City.country; 
                        
                        $reactions.transition("/GetDate");
                        }
                    else 
                        $reactions.transition("/GetCity");
                        
        
    state: GetCity
        random:
            a: Укажите, пожалуйста, название города, для которого хотите узнать прогноз погоды.
            a: Скажите, пожалуйста, для какого города вы хотетие получить прогноз?
            a: Прогноз для какого города хотите получить?
            
        state: UserCity
            q: * $City *
            script:
                $session.stateCounterInARow = 0;
                if ($parseTree._City) {
                    $session.userCity = $parseTree._City.name;
                    $session.lon = $parseTree._City.lon;
                    $session.lat = $parseTree._City.lat;
                    $session.country = $parseTree._City.country;                    
                    }
                    
                if ($parseTree["_duckling.date"])
                    $reactions.transition("/CheсkDate");
                else
                    $reactions.transition("/GetDate");
        
        state: LocalCatchAll || noContex = true
            event: noMatch
            script:
                $session.stateCounterInARow++
                
            if: $session.stateCounterInARow < 3
                random: 
                    a: Извините, не совсем понял вас. Напишите, пожалуйста, название города, чтобы я смог узнать прогноз погоды для него.
                    a: К сожалению, не понял вас. Укажите, пожалуйста, нужный вам город?
            else:
                a: Простите! Кажется, я пока не умею узнавать прогноз погоды с такими параметрами, но постараюсь поскорее научиться.
                
                script: 
                    $session.userDate = null
                    $session.stateCounterInARow = 0
                    
                go!: /SomethingElse
                
    state: GetDate
        random:
            a: На какую дату требуется прогноз?
            a: Прогноз погоды на какую дату вам нужен?
            
        state: UserDate
            q: * @duckling.date *
            script:
                $session.stateCounterInARow = 0;
                
                if ($parseTree["_duckling.date"]) {
                    $session.userDate = new Date($parseTree["_duckling.date"].year + "/"+ $parseTree["_duckling.date"].month + "/"+ $parseTree["_duckling.date"].day);
                    $reactions.transition("/CheсkDate");
                    }
                    
        state: LocalCatchAll || noContex = true
            event: noMatch
            script:
                $session.stateCounterInARow ++
                
            if: $session.stateCounterInARow < 3
                random: 
                    a: Извините, не совсем понял вас. Напишите, пожалуйста, нужную вам дату.
                    a: К сожалению, не понял вас. Введите, пожалуйста, дату, которая вам нужна.
            else:
                a: Простите! Кажется, я пока не умею узнавать прогноз погоды с такими параметрами, но постараюсь поскорее научиться.
                
                script: 
                    $session.stateCounterInARow = 0;
                    $session.userDate = null
                    $session.userCity = null;
                    $session.lat = null;
                    $session.lon = null;
                    $session.country = null;
                    
                go!: /SomethingElse
          
    state: CheсkDate
        script:
            var date = new Date();
            var userDate = $session.userDate;
            
            if (userDate.setHours(0,0,0,0) < date.setHours(0,0,0,0)) {
                $session.stateCounter = 0;
                $reactions.transition("/ThisDayHasPassed");
                } 
                else if (DatesDiff(userDate, date) > 5) {
                    $session.stateCounter = 0;
                    $reactions.transition("/ThisDayIsNotComingSoon");
                    } else $reactions.transition("/TellWeather");
        
    state: ThisDayHasPassed
        script:
            $session.stateCounter ++
                
        if: $session.stateCounter < 3
            script:
                $session.userDate = null;
            random: 
                a: К сожалению, я не могу узнать прогноз погоды на период времени в прошлом.
                a: Я не смогу посмотреть прогноз для прошедшего периода.
            go!: /GetDate
        else:
            a: Простите! Кажется, я пока не умею узнавать прогноз погоды с такими параметрами, но постараюсь поскорее научиться.
                
            script: 
                $session.stateCounter = 0;
                $session.userDate = null;
                $session.userCity = null;
                $session.lat = null;
                $session.lon = null;
                $session.country = null;
                    
            go!: /SomethingElse
            
    state: ThisDayIsNotComingSoon
        script:
            $session.stateCounter ++
                
        if: $session.stateCounter < 3
            script:
                $session.userDate = null;
            random: 
                a: Мне жаль, но метеорологи и я пока не можем дать такие долгосрочные прогнозы.
                a: Извините, посмотреть прогноз на такую далекую дату я не смогу.
            go!: /GetDate
        else:
            a: Простите! Кажется, я пока не умею узнавать прогноз погоды с такими параметрами, но постараюсь поскорее научиться.
                
            script: 
                $session.stateCounter = 0;
                $session.userDate = null;
                $session.userCity = null;
                $session.lat = null;
                $session.lon = null;
                $session.country = null;
                    
            go!: /SomethingElse
        
            
    state: TellWeather
        script:
            $temp.response = openWeatherMapCurrent("metric","ru",$session.lat, $session.lon);
            moment.lang('ru');
            $temp.userFormatDate = moment($session.userDate).format('LL');
        if: $temp.response.isOk
            random:
                a: У меня получилось уточнить: на {{ $temp.userFormatDate }} в {{capitalize($nlp.inflect($session.userCity, "loct"))}} температура воздуха составит {{ Math.floor($temp.response.data.main.temp)}} {{ $nlp.conform("градус", Math.floor($temp.response.data.main.temp)) }} по Цельсию.
                a: Смог узнать для вас прогноз: на {{ $temp.userFormatDate }} в {{capitalize($nlp.inflect($session.userCity, "loct"))}} будет {{Math.floor($temp.response.data.main.temp)}} {{ $nlp.conform("градус", Math.floor($temp.response.data.main.temp))}} по Цельсию.
        else:
            a: У меня не получилось узнать погоду. Попробуйте ещё раз.
            script:
                $session.stateCounter = 0;
                
        state: Error
            event: noMatch
            
            script:
                $session.stateCounter++
                
            if: $session.stateCounter < 3
                go!: /TellWeather
            else:
                a: Мне очень жаль, но при обращении к сервису, содержащему сведения о погоде, произошла ошибка. Пожалуйста, попробуйте написать мне немного позже. Надеюсь работоспособность сервиса восстановится.
                script:
                    $session.stateCounter = 0;
                    $session.userDate = null;
                    $session.userCity = null;
                    $session.lat = null;
                    $session.lon = null;
                    $session.country = null;
                    
                go!: /SomethingElse
               
    state: TravelRequest
        intent!: /tour
        random:
            a: Готов помочь вам оформить заявку на подбор тура. Как только я соберу от вас нужные для запроса данные, наш менеджер подберет самые подходящие варианты и свяжется с вами.
            a: Рад помочь с оформлением запроса на подбор тура. Как только мы заполним заявку, наш специалист свяжется с вами, чтобы предложить наиболее подходящие варианты путешествий.
        script:
            $session.stateCounter = 0;
        
        if: $session.country
            go!: /AskNumberOfPeople
        else:
            a: Подскажите, вы уже определились с страной прибытия?
            
        state: Agree
            q: * @Countries *
            q: * (да|ага|yes|ога) *
            script: 
                $session.stateCounterInARow = 0;
                if ($parseTree._Countries) {
                $session.country = $parseTree._Countries.name;   
                }
                    
            if: $session.country
                a: Отлично, я передам консультанту, что местом пребывания станет {{$session.country}}. А теперь, давайте перейдем к указанию оставшихся параметров.
                go!: /AskNumberOfPeople
            else:
                a: Введите название страны
                    
            state: Country
                q: * @Countries *
                script: 
                    $session.country = $parseTree._Countries.name;  
                a: Отлично, я передам консультанту, что местом пребывания станет {{$session.country}}. А теперь, давайте перейдем к указанию оставшихся параметров.    
                go!: /AskNumberOfPeople
                    
            state: LocalCatchAll || noContext = true
                event: noMatch
                script:
                    $session.stateCounter ++;
                
                if: $session.stateCounter < 3
                    random:
                        a: Извините, не совсем понял вас. Назовите, пожалуйста, нужную вам страну.
                        a: К сожалению, не понял вас. Введите название страны для поездки.
                else:
                    script:
                        $session.stateCounter = 0
                        $session.country = "Не указано";
                    a: Простите! Так и не получилось вас понять. Когда консультант получит заявку, он подберет варианты стран для вас. А теперь давайте перейдем к указанию оставшихся параметров.
                    go!: /AskNumberOfPeople
               
        state: Disagree
            q: * нет * 
            a: Понял вас. В таком случае, когда консультант получит заявку, он подберет варианты стран для вас. А теперь давайте перейдем к указанию оставшихся параметров.
            script:
                $session.country = "Не указано";  
            go!: /AskNumberOfPeople
                
        state: LocalCatchAll
                event: noMatch
                script:
                    $session.stateCounterInARow ++
                
                if: $session.stateCounterInARow < 3
                    random:
                        a: Извините, не совсем понял вас. Подскажите, вы выбрали страну для путешествия?
                        a: К сожалению, не понял вас. Вы выбрали страну для поездки?
                    script:
                        $reactions.transition("/TravelRequest");
                else:
                    script:
                        $session.stateCounterInARow = 0;
                        $session.country = "Не указано";
                    a: Простите! Так и не получилось вас понять. Когда консультант получит заявку, он подберет варианты стран для вас. А теперь давайте перейдем к указанию оставшихся параметров.
                    go!: /AskNumberOfPeople
                  
    state: AskNumberOfPeople
        a: Укажите количество человек, которые отправятся в путешествие.
        script:
            $session.stateCounterInARow = 0;
        
        state: Number
            q: * @duckling.number *
            script:
                if ($parseTree["_duckling.number"] > 0) {
                    $session.numberOfPeople = $parseTree["_duckling.number"];
                    $reactions.transition("/AskStartDate");
                } 
                
        state: DontKnow  
            intent: /незнаем
            script:
                $session.numberOfPeople = "Не указано";
                $reactions.transition("/AskStartDate");
                
        state: LocalCatchAll || noContext = true
            event: noMatch
            script:
                $session.stateCounterInARow ++;
                
            if: $session.stateCounterInARow < 3
                script:
                    if ($parseTree["_duckling.number"]) {
                        $reactions.answer("К сожалению, не могу принять такой ответ. Пожалуйста, введите валидное число людей - оно должно быть больше 0.");
                        }
                    else {
                        var answers = ["Извините, не совсем понял вас. Сколько человек планирует отправиться в поездку?",
                        "К сожалению, не понял вас. Сколько человек поедет в тур?"];
                        var randomAnswer = answers[$reactions.random(answers.length)];
                        $reactions.answer(randomAnswer);
                        }
            else: 
                script:
                    $session.stateCounterInARow = 0;
                go!: /AskNumberOfPeople/DontKnow

    state: AskStartDate
        a: Еще мне потребуется предполагаемая дата начала поездки. Пожалуйста, напишите ее.
        script:
            $session.stateCounterInARow = 0;
            
        state: Date
            q: * @duckling.date *
            script:
                log("///////// MY LOG "+toPrettyString($parseTree));
                
                if ($parseTree["_duckling.date"]) {
                    $session.startDate = new Date($parseTree["_duckling.date"].year + "/"+ $parseTree["_duckling.date"].month + "/"+ $parseTree["_duckling.date"].day);
                    
                    var date = new Date();
                    var userDate = $session.startDate;
                    if (userDate.setHours(0,0,0,0) < date.setHours(0,0,0,0)) {
                        $reactions.transition("/AskStartDate/LocalCatchAll");
                    }  else {
                        $reactions.transition("/AskDuration");
                       }
                }
                    
        state: DontKnow  
            intent: /незнаем
            script:
                $session.startDate = "Не указано";
                $reactions.transition("/AskDuration");
                
        state: LocalCatchAll
            event: noMatch
            script:
                $session.stateCounterInARow ++
                
            if: $session.stateCounterInARow < 3
                script:
                    if ($parseTree["_duckling.date"]) {
                        $reactions.answer("К сожалению, не могу принять такой ответ. Пожалуйста, введите актуальную дату - она не должна быть в прошедшем периоде.");
                        }
                    else {
                        var answers = ["Извините, не совсем понял вас. Какого числа предполагаете выезд?",
                        "К сожалению, не понял вас. На какую дату планируете отправление?"];
                        var randomAnswer = answers[$reactions.random(answers.length)];
                        $reactions.answer(randomAnswer);
                        }
            else:
                script: 
                    $session.stateCounterInARow = 0;
                    $reactions.transition("/AskStartDate/DontKnow");
                    
    state: AskDuration
        a: Также укажите, сколько дней будет длиться путешествие.
        script:
            $session.stateCounterInARow = 0;
       
        state: Number
            q: * @duckling.number *
            intent: /неделя
            script:
                log("!!!///////// MY LOG "+toPrettyString($parseTree));
                if ($parseTree["_duckling.number"] > 0) {
                    $session.countDays = $parseTree["_duckling.number"];
                    $session.endDate = addDays($session.startDate, $parseTree["_duckling.number"]);
                    $reactions.transition("/AskServices");
                } else if ($parseTree["pattern"]) {
                    $session.countDays = 7;
                    $session.endDate = addDays($session.startDate, 7);
                    $reactions.transition("/AskServices");
                    } else {
                        $reactions.transition("/AskDuration/LocalCatchAll");
                    }
                      
        state: DontKnow  
            intent: /незнаем
            script:
                $session.endDate = "Не указано";
                $reactions.transition("/AskServices");
                    
        state: LocalCatchAll || noContex = true
            event: noMatch
            script:
                $session.stateCounterInARow ++
                
            if: $session.stateCounterInARow < 3
                script:
                    if ($parseTree["_duckling.number"]) {
                        $reactions.answer("К сожалению, не могу принять такой ответ. Пожалуйста, введите валидное число дней - оно должно быть больше 0.");
                        }
                    else {
                        var answers = ["Извините, не совсем понял вас. Сколько дней планируете быть в поездке?",
                        "К сожалению, не понял вас. На какой срок планируете отъезд?"];
                        var randomAnswer = answers[$reactions.random(answers.length)];
                        $reactions.answer(randomAnswer);
                        }
            else:
                script: 
                    $session.stateCounterInARow = 0;
                    $reactions.transition("/AskDuration/DontKnow");
            
    state: AskServices
        a: Уточните, пожалуйста, какой пакет услуг вам интересен?
        script: 
            $session.stateCounterInARow = 0;
        buttons:
            "Эконом" -> /AskServices/Package
            "Стандарт" -> /AskServices/Package
            "VIP" -> /AskServices/Package
            
        state: Package
            q: @Packages  
            script:
                $session.services = $request.query;
            go!: /AskName
                
        state: WhatIsIncluded
            intent: /included
            script:
                if ($parseTree._Packages) {
                    var answer = "В пакет услуг \""+$parseTree._Packages.name+"\" входят следующие опции: "+ $parseTree._Packages.consists +".";
                    $reactions.answer(answer);
                    }
                else {
                    
                    var pk1 = JSON.parse($caila.entitiesLookup("эконом", true).entities[0].value);
                    var pk2 = JSON.parse($caila.entitiesLookup("стандарт", true).entities[0].value);
                    var pk3 = JSON.parse($caila.entitiesLookup("vip", true).entities[0].value);
                    
                    var answer = "Пакет \""+pk1.name+"\" включает следующие опции: " +pk1.consists +". В пакет \""+pk2.name+"\" входят: " +pk2.consists +". И, наконец, \""+pk3.name+"\" предполагает " +pk3.consists +".";
                    $reactions.answer(answer);
                    }
            
            go!: /AskServices
            
        state: Price
            intent: /prices
            script:
                if ($parseTree._Packages) {
                    if ($session.numberOfPeople !== "Не указано") {
                        if ($session.endDate !== "Не указано") {
                        $session.personalPrice = $session.numberOfPeople * $parseTree._Packages.perDayOneMan*$session.countDays;
                        var answer = "При оформлении пакета услуг \""+$parseTree._Packages.name+"\" на поездку для " +
                        $session.numberOfPeople +" "+ $nlp.conform("человек", $session.numberOfPeople) +" стоимость составит "+$session.personalPrice+ " "+$nlp.conform("рублей", $session.personalPrice)+".";
                        $reactions.answer(answer);
                        }
                    } else {
                        var answer = "При оформлении пакета услуг \""+$parseTree._Packages.name+" стоимость составит "+$parseTree._Packages.perDayOneMan+ " рублей на одного человека.";
                        $reactions.answer(answer);
                        }
                } else {
                    var pk1 = JSON.parse($caila.entitiesLookup("эконом", true).entities[0].value);
                    var pk2 = JSON.parse($caila.entitiesLookup("стандарт", true).entities[0].value);
                    var pk3 = JSON.parse($caila.entitiesLookup("vip", true).entities[0].value);
                    
                    var answer = "При формировании пакета услуг \""+ pk1.name+"\" стоимость составит "+ pk1.perDayOneMan+ " рублей на одного человека. Для пакета \""+ pk2.name+"\" - "+ pk2.perDayOneMan+ ". А \""+ pk3.name+"\" будет стоить "+ pk3.perDayOneMan+" за одного человека.";
                    $reactions.answer(answer);
                    }
            go!: /AskServices
            
        state: LocalCatchAll || noContext = true
            event: noMatch
            intent: /незнаем
            script:
                $session.stateCounterInARow ++
                
            if: $session.stateCounterInARow < 3
                script:
                    if ($parseTree["pattern"]) {
                        $reactions.answer("Мне жаль, но без указания пакет услуг я не смогу отправить заявку. Сделайте выбор, пожалуйста.");
                        }
                    else {
                        var answers = ["Извините, не совсем понял вас. Какой пакет услуг вам больше всего подходит?",
                        "К сожалению, не понял вас. Какой пакет услуг выбираете?"];
                        var randomAnswer = answers[$reactions.random(answers.length)];
                        $reactions.answer(randomAnswer);
                        $reactions.buttons([{ text: "Эконом", transition: "/AskServices/Package" },
                        {text: "Стандарт", transition: "/AskServices/Package"},
                        {text: "VIP", transition: "/AskServices/Package"}])
                        }
            else:
                script: 
                    $session.stateCounterInARow = 0;
                    var answer = "К сожалению, без выбора пакета заявка не может быть отправлена. Вы можете вернуться к её заполнению позже или связаться с нами по номеру 8 (812) 000-00-00.";
                    $reactions.answer(answer);
                    $reactions.transition("/SomethingElse");
            
    
    state: AskName
        if: $client.name
            script:
                $reactions.transition("/AskPhone");
        else:
            script:
                if ($context.session.lastState !== "/AskName/LocalCatchAll"){
                    var answer = "С параметрами заявки почти закончили! Осталось указать контакты, чтобы менеджер смог связаться с вами.";
                    $reactions.answer(answer);
                }
                else {
                    var answer = "Введите, пожалуйста, ваше имя.";
                    $reactions.answer(answer);
                    }
        script:
            $session.stateCounterInARow = 0;
                    
        state: Name
            q: * @pymorphy.name *
            q: * меня зовут * * $Name *
            q: * зови меня * * $Name *
            q: * имя * * $Name *
            q: * ладно * * $Name *
            q: * я * * $Name *
            script:
                log("!!!///////// MY LOG "+toPrettyString($parseTree));
                if ($parseTree["_pymorphy.name"]) {
                    $client.name = capitalize($parseTree["_pymorphy.name"]);
                    $session.userName = capitalize($parseTree["_pymorphy.name"]);
                    } else if ($parseTree["pattern"] && ($parseTree["_Root"] !== "да")) {
                    $session.userName = capitalize($parseTree["value"].name);
                    } else {
                        $session.userName = $session.userName;
                        $client.name = $session.userName;
                        }
                    
            if: $client.name
                script:
                    //$reactions.answer($client.name);
                    
            if: $session.userName
                script:
                    //$reactions.answer($session.userName);
            go!: /AskPhone    
            
        state: LocalCatchAll || noContext = true
            event: noMatch
            intent: /незнаем
            intent: /неХочуУказывать
            intent: /зачем
            script:
                $session.stateCounterInARow ++
                
            if: $session.stateCounterInARow < 2
                script:
                    if ($parseTree["pattern"]) {
                        $reactions.answer("Мне жаль, но без указания вашего имени отправить заявку не получится. Укажите его, пожалуйста.");
                        }
                    else {
                        $session.userName = $request.query;
                        $reactions.transition("/UnusualName");    
                    }
            else:
                script: 
                    $session.stateCounterInARow = 0;
                    var answer = "К сожалению, без указания вашего имени заявка не может быть отправлена. Вы можете вернуться к ее заполнению позже или связаться с нами по номеру 8 (812) 000-00-00.";
                    $reactions.answer(answer);
                    $reactions.transition("/SomethingElse");       
            
    state: UnusualName   
        a: Как необычно! Подскажите, вы точно хотели указать в качестве своего имени "{{ $request.query }}"?
        script: 
            $session.userName = $request.query;
        
        state: ChoosenNo
            q: * нет *
            script:
                $reactions.transition("/AskName");       
                
        state: ChoosenYes
            q: * да *
            q: * $Name *
            event: noMatch
            script:
                $reactions.transition("/AskName/Name");  
                
    state: AskPhone
        script:
            $session.stateCounterInARow = 0;
        if: $client.phone
            go!: AskComment
        else:
            a: Укажите номер телефона для связи.
            script:
                $reactions.buttons({ text: "Поделиться контактом", request_contact: true })
                
        state: Phone
            event: telegramSendContact
            script:
                $client.phone_number = $request.rawRequest.message.contact.phone_number;
            a: Спасибо! Наш менеджер свяжется с вами по номеру телефона {{ $client.phone_number }}.
            
        state: LocalCatch || noContext = true
            event: noMatch
            intent: /незнаем
            intent: /неХочуУказывать
            intent: /зачем
            script:
                $session.stateCounterInARow ++
                
            if: $session.stateCounterInARow < 3
                script:
                    if ($parseTree["pattern"]) {
                        $reactions.answer("Мне жаль, но без указания вашего контактного номера одтправить заявку не получится. Укажите его, пожалуйста.");
                        }
                    else {
                        var answers = ["Извините, не совсем понял вас. Для заявки требуется ваш контактный номер, поэтому, пожалуйста, укажите его.",
                        "К сожалению, не смог распознать номер телефона в вашем ответе. Пожалуйста, укажите его."];
                        var randomAnswer = answers[$reactions.random(answers.length)];
                        $reactions.answer(randomAnswer);
                    }
            else:
                script: 
                    $session.stateCounterInARow = 0;
                    var answer = "К сожалению, без указания вашего номера телефон заявка не может быть отправлена. Вы можете вернуться к ее заполнению позже или связаться с нами по номеру 8 (812) 000-00-00.";
                    $reactions.answer(answer);
                    $reactions.transition("/SomethingElse");      
            
    state: AskComment
        a: Теперь напишите комментарий для менеджера, если это требуется.
        buttons:
            "Не нужно" -> /AskComment/Disagree
            
        state: Comment
            event: noMatch
            script:
                $session.userComment = $request.query;
            go!: /Confirmation
                
        state: Disagree
            intent: /незнаем
            intent: /неХочуУказывать
            intent: /зачем
            q: * нет *
            script:
                $session.userComment = "Не указано";
            go!: /Confirmation
    
    state: Confirmation
        script:
            $temp.confirmation = "Среди важных критериев подбора вы выделили: "
                
    state: DontHaveQuestions
        q!: * вопросов нет *
        q!: * нет *
        q!: * У меня больше нет вопросов *
        random:
            a: Вас понял!
            a: Хорошо!
            a: Понял!
        go!: /GoodBye               
              
    state: SomethingElse  
        random:
            a: Хотите спросить что-то ещё?
            a: Могу ли я помочь чем-то ещё?
            a: Подскажите, у вас остались ещё вопросы?
            
        buttons:
            "Узнать прогноз погоды" -> /WeatherForecast
            "Оформить заявку на подбор тура" -> /TravelRequest
                
        state: CatchCallbackButton
            event: telegramCallbackQuery
            script:
                $temp.goTo = $request.query
            go!: {{$temp.goTo}}
            
        state: LocalCatchAll || noContex = true
            event: noMatch
            
            script:
                $session.stateCounterInARow++
                
            if: $session.stateCounterInARow < 3
                random: 
                    a: Извините, не совсем понял. Пожалуйста, подскажите, могу ли я чем-то вам помочь?
                    a: К сожалению, не смог понять, что вы имеете в виду. Подскажите, что вас интересует?
                    
                buttons:
                    "Узнать прогноз погоды" -> /WeatherForecast
                    "Оформить заявку на подбор тура" -> /TravelRequest
            else:
                script:
                    $session.stateCounterInARow = 0
                a: Простите, так и не смог понять, что вы имели ввиду.
                go!: /GoodBye
                
            state: CatchCallbackButton
                event: telegramCallbackQuery
                script:
                    $temp.goTo = $request.query
                go!: {{$temp.goTo}}
              
    state: GoodBye
        intent!: /пока
        random:
            a: Всего доброго!
            a: Всего вам доброго!
            a: Всего доброго, до свидания!
               
    