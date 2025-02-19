
require: slotfilling/slotFilling.sc
  module = sys.zb-common
require: city/city.sc
    module = sys.zb-common  
require: name/name.sc
    module = sys.zb-common
  
require: funcs.js
require: moment.js
require: moment-with-locales.js
require: patterns.sc
  
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
        });

    state: Start
        q!: $regex</start>
        q!: старт
        q!: * $hello *
        image: https://media.istockphoto.com/id/511095951/ru/%D1%84%D0%BE%D1%82%D0%BE/%D0%BE%D0%BD-%D0%B7%D0%B4%D0%B5%D1%81%D1%8C-%D1%87%D1%82%D0%BE%D0%B1%D1%8B-%D0%BF%D0%BE%D0%BC%D0%BE%D1%87%D1%8C.jpg?s=2048x2048&w=is&k=20&c=86_eS2vtvuPqNIFl04rO9yg1N7bv9yQMpqIrM0SNOH4=
        script:
            $context.session = {};
            $session = {};
            $session.userHasTour = false;
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
            
    state: AreYouRobot
        intent!: /robot
        random:
            a: Я Артур - бот-помощник компании Just Tour, всегда готов отвечать на ваши вопросы.
            a: Вы общаетесь с Артуром - чат-ботом, разработанным командой Just Tour, чтобы помогать вам. Всегда рад пообщаться с вами!
        go!: /SomethingElse
    
    state: WhatCanYouDo
        intent!: /whatcanyoudo
        random:
            a: Умею рассказывать о погоде в городах мира и составлять заявки на подбор подходящего именно вам путешествия.
            a: С удовольствием расскажу вам о ближайших метеопрогнозах для разных городов и помогу составить запрос на подбор тура.
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
        intent: /weather
        q!: * погода * {$City * * @duckling.date} *
        q!: * погода * $City *
        script:
            
            log("!!!///////// MY LOG "+toPrettyString($parseTree));
            
            if ($parseTree._City && $parseTree["_duckling.date"]) {
                log("1!!!///////// MY LOG "+toPrettyString($parseTree));
                
                $session.userCity = $parseTree._City.name;
                $session.lon = $parseTree._City.lon;
                $session.lat = $parseTree._City.lat;
                
                if ($caila.entitiesLookup($parseTree._City.country, true) != null) {
                    if ($caila.entitiesLookup($parseTree._City.country, true).entities.length) {
                        var pk = JSON.parse($caila.entitiesLookup($parseTree._City.country, true).entities[0].value);
                        $session.country = pk.name;
                    } else {
                        $session.country = null;
                        }
                }
               
                $session.userDate = new Date($parseTree["_duckling.date"].year + "/"+ $parseTree["_duckling.date"].month + "/"+ $parseTree["_duckling.date"].day);
                $reactions.transition("/WeatherForecast/CheсkDate");
            }
            
            else  
                if ($parseTree["_duckling.date"]) {
                    log("2!!!///////// MY LOG "+toPrettyString($parseTree));
                    $session.userDate = new Date($parseTree["_duckling.date"].year + "/"+ $parseTree["_duckling.date"].month + "/"+ $parseTree["_duckling.date"].day);
                    
                    $reactions.answer($session.userDate);
                    
                    $reactions.transition("/WeatherForecast/GetCity");
                    }
                else 
                    if ($parseTree._City) {
                        log("3!!!///////// MY LOG "+toPrettyString($parseTree));
                        $session.userCity = $parseTree._City.name;
                        $session.lon = $parseTree._City.lon;
                        $session.lat = $parseTree._City.lat;
                        
                        if ($caila.entitiesLookup($parseTree._City.country, true) != null) { 
                            if ($caila.entitiesLookup($parseTree._City.country, true).entities.length) {
                                var pk = JSON.parse($caila.entitiesLookup($parseTree._City.country, true).entities[0].value);
                                $session.country = pk.name;
                            } else {
                                $session.country = null;
                            }
                       }
                        
                        $reactions.transition("/WeatherForecast/GetDate");
                    }
                    else 
                    {
                        
                        log("4!!!///////// MY LOG "+toPrettyString($parseTree));
                        $reactions.transition("/WeatherForecast/GetCity");
                    }
            
    
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
                    
                        if ($caila.entitiesLookup($parseTree._City.country, true) != null) {
                            if ($caila.entitiesLookup($parseTree._City.country, true).entities.length) {
                                var pk = JSON.parse($caila.entitiesLookup($parseTree._City.country, true).entities[0].value);
                                $session.country = pk.name;
                            } else {
                                $session.country = null;
                                }
                        }
                    }
                    
                    $reactions.answer($session.userDate);
                    if ($session.userDate)
                        $reactions.transition("/WeatherForecast/CheсkDate");
                    else
                        $reactions.transition("/WeatherForecast/GetDate");
        
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
                        $reactions.transition("/WeatherForecast/CheсkDate");
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
                        $reactions.transition("/WeatherForecast/ThisDayIsNotComingSoon");
                        } else { 
                            $session.stateCounter = 0;
                            $reactions.transition("/WeatherForecast/TellWeather") 
                            };
        
        state: ThisDayHasPassed
            script:
                $session.stateCounter ++
                
            if: $session.stateCounter < 3
                script:
                    $session.userDate = null;
                random: 
                    a: К сожалению, я не могу узнать прогноз погоды на период времени в прошлом.
                    a: Я не смогу посмотреть прогноз для прошедшего периода.
                go!: /WeatherForecast/GetDate
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
                go!: /WeatherForecast/GetDate
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
                moment.lang('ru'); 
                $temp.userFormatDate = moment($session.userDate).format('LL');
                openWeatherMapCurrent("metric","ru",$session.lat, $session.lon).then(function (res) {
                    
                    var answers = [
                        "У меня получилось уточнить: на "+ $temp.userFormatDate +" в "+ capitalize($nlp.inflect($session.userCity, "loct"))+" температура воздуха составит "+ Math.floor(res.main.temp)+ " "+ $nlp.conform("градус", Math.floor(res.main.temp)) +" по Цельсию.",
                        "Смог узнать для вас прогноз: на "+ $temp.userFormatDate +" в "+ capitalize($nlp.inflect($session.userCity, "loct"))+" будет "+Math.floor(res.main.temp)+ " "+$nlp.conform("градус", Math.floor(res.main.temp)) +" по Цельсию."
                    ];
                    var randomAnswer = answers[$reactions.random(answers.length)];
                    $reactions.answer(randomAnswer);
                    
                }).catch(function (err) {
                     $reactions.transition("/WeatherForecast/TellWeather/Error");
                });
                
            if: $session.country
                if: $session.userHasTour 
                    go!: /WeatherForecast/SomethingElseForWeather
                else:
                    go!: /WeatherForecast/OfferTour
            else:  
                go!: /WeatherForecast/SomethingElseForWeather
                    
            state: Error
                script:
                    $session.stateCounter++
                
                if: $session.stateCounter < 3
                    go!: /WeatherForecast/TellWeather
                else:
                    a: Мне очень жаль, но при обращении к сервису, содержащему сведения о погоде, произошла ошибка. Пожалуйста, попробуйте написать мне немного позже. Надеюсь работоспособность сервиса восстановится.
                    script:
                        $session.stateCounter = 0;
                        $session.userDate = null;
                        $session.userCity = null;
                        $session.lat = null;
                        $session.lon = null;
                    go!: /SomethingElse
                
        state: SomethingElseForWeather
            script:
                $session.stateCounterInARow = 0;
            random:
                a: Хотите спросить что-то ещё?
                a: Могу ли я помочь чем-то ещё?
                a: Подскажите, у вас остались ещё вопросы?
            buttons:
                "Узнать прогноз с другими параметрами" -> /WeatherForecast
                "Оформить заявку на подбор тура" -> /TravelRequest
            
            state: AnotherOne
                intent: /SomethingElseForWeather
                q: * {$City * * @duckling.date} *
                q: * а в городе $City *
                script:
                    if (($parseTree._City) && ($parseTree["_duckling.date"])) {
                        $session.userCity = $parseTree._City.name;
                        $session.lon = $parseTree._City.lon;
                        $session.lat = $parseTree._City.lat;
                        $session.country = $parseTree._City.country;   
                        $session.userDate = new Date($parseTree["_duckling.date"].year + "/"+ $parseTree["_duckling.date"].month + "/"+ $parseTree["_duckling.date"].day);
                        $reactions.transition("/WeatherForecast/CheсkDate");
                        }
                    else 
                        if ($parseTree["_duckling.date"]) {
                            $session.userDate = new Date($parseTree["_duckling.date"].year + "/"+ $parseTree["_duckling.date"].month + "/"+ $parseTree["_duckling.date"].day);
                            $reactions.transition("/WeatherForecast/GetCity");
                        }
                        else 
                            if ($parseTree._City) {
                                $session.userCity = $parseTree._City.name;
                                $session.lon = $parseTree._City.lon;
                                $session.lat = $parseTree._City.lat;
                                $session.country = $parseTree._City.country; 
                        
                                $reactions.transition("/WeatherForecast/GetDate");
                            }
                            else 
                                $reactions.transition("/WeatherForecast/GetCity");
                            
            state: Agree
                q: * да * || fromState = "/WeatherForecast/SomethingElseForWeather/AnotherOne", onlyThisState = true
                script:
                    $session.userCity = null;
                    $session.lon = null;
                    $session.lat = null;
                    $session.country = null;   
                    $session.userDate = null;
                go!: /HowCanIHelpYou
                
            state: DisAgree
                q: * нет * || fromState = "/WeatherForecast/SomethingElseForWeather/AnotherOne", onlyThisState = true
                go!: /DontHaveQuestions
            
            state: LocalCatchAll || noContext = true
                event: noMatch
                script:
                    $session.stateCounterInARow ++;
                
                if: $session.stateCounterInARow < 3
                    random:
                        a: Извините, не совсем понял. Пожалуйста, подскажите, могу ли я ещё чем-то помочь?
                        a: К сожалению, не смог понять, что вы имеете в виду. Подскажите, остались ли у вас ещё вопросы?
                    buttons:
                        "Узнать прогноз с другими параметрами" -> /WeatherForecast
                        "Оформить заявку на подбор тура" -> /TravelRequest
                else:
                    script:
                        $session.stateCounterInARow = 0
                    a: Простите, так и не смог понять, что вы имели в виду.
                    go!: /Goodbye
        
        state: OfferTour
            script:
                $session.stateCounter = 0;
            random:
                a: Хотите оставить заявку на подбор тура в {{ capitalize($nlp.inflect($session.country, "accs")) }}?
                a: Можем составить заявку на подбор идеального тура в {{ capitalize($nlp.inflect($session.country, "accs"))}}. Хотите?
            
            state: OfferTourYes
                q: * (да|хочу) * || fromState = "/WeatherForecast/OfferTour", onlyThisState = true
                go!: /TravelRequest
            
            state: Disagree 
                q: * (нет|не хочу) * || fromState = "/WeatherForecast/OfferTour", onlyThisState = true
                a: Понял вас!
                script:
                    $session.userCity = null;
                    $session.userDate = null;
                    $session.lat = null;
                    $session.lon = null;
                    $session.country = null;
                    $session.stateCounterDisagree = 0;
                a: В таком случае, желаете узнать погоду в другом городе мира?
            
                state: DisagreeYes
                    q: * (да|хочу) * || fromState = "/WeatherForecast/OfferTour/Disagree", onlyThisState = true
                    go!: /WeatherForecast
                
                state: DisagreeNo
                    q: * (нет|не хочу) * || fromState = "/WeatherForecast/OfferTour/Disagree", onlyThisState = true
                    go!: /SomethingElse
                
                    state: LocalCatchAll || noContext = true
                        event: noMatch
                        script:
                            $session.stateCounterDisagree ++;
                        if: $session.stateCounterDisagree < 2
                            a: Простите, не совсем понял. Хотите узнать прогноз погоды для другого города?   
                            go: /WeatherForecast/OfferTour/Disagree
                        else
                            script:
                                $session.stateCounterDisagree = 0;
                            go!: /SomethingElse
            
            state: LocalCatchAll || noContext = true
                event: noMatch
                script:
                    $session.stateCounter ++;
                if: $session.stateCounter < 2
                    a: Извините, не совсем понял вас, вы желаете оставить запрос на подбор путевки в {{$session.country}}?
                else:
                    script:
                        $session.userCity = null;
                        $session.userDate = null;
                        $session.lat = null;
                        $session.lon = null;
                        $session.country = null;
                        $session.stateCounter = 0;
                    go!: /SomethingElse
    
    state: TravelRequest
        intent!: /tour
        random:
            a: Готов помочь вам оформить заявку на подбор тура. Как только я соберу от вас нужные для запроса данные, наш менеджер подберет самые подходящие варианты и свяжется с вами.
            a: Рад помочь с оформлением запроса на подбор тура. Как только мы заполним заявку, наш специалист свяжется с вами, чтобы предложить наиболее подходящие варианты путешествий.
        script:
            $session.stateCounter = 0;
        
        if: $session.country
            go!: /TravelRequest/AskNumberOfPeople
        else:
            a: Подскажите, вы уже определились с страной прибытия?
            
        state: Agree
            q: * @CodeCounties *
            q: * (да|ага|yes|ога) * || fromState = "/TravelRequest", onlyThisState = true
            script: 
                $session.stateCounterInARow = 0;
                if ($parseTree._CodeCounties) {
                $session.country = $parseTree._CodeCounties.name;   
                }
                    
            if: $session.country
                a: Отлично, я передам консультанту, что местом пребывания станет {{$session.country}}. А теперь, давайте перейдем к указанию оставшихся параметров.
                go!: /TravelRequest/AskNumberOfPeople
            else:
                a: Введите название страны
                    
            state: Country
                q: * @CodeCounties *
                script: 
                    $session.country = $parseTree._CodeCounties.name;  
                a: Отлично, я передам консультанту, что местом пребывания станет {{$session.country}}. А теперь, давайте перейдем к указанию оставшихся параметров.    
                go!: /TravelRequest/AskNumberOfPeople
                    
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
                    go!: /TravelRequest/AskNumberOfPeople
               
        state: Disagree
            q: * нет * || fromState = "/TravelRequest", onlyThisState = true
            a: Понял вас. В таком случае, когда консультант получит заявку, он подберет варианты стран для вас. А теперь давайте перейдем к указанию оставшихся параметров.
            script:
                $session.country = "Не указано";  
            go!: /AskNumberOfPeople
                
        state: LocalCatchAll || noContext = true
                event: noMatch
                script:
                    $session.stateCounterInARow ++
                
                if: $session.stateCounterInARow < 3
                    random:
                        a: Извините, не совсем понял вас. Подскажите, вы выбрали страну для путешествия?
                        a: К сожалению, не понял вас. Вы выбрали страну для поездки?
                    go: /TravelRequest
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
                q: * только я *
                script:
                    if ($parseTree["_duckling.number"] > 0)  {
                        $session.numberOfPeople = $parseTree["_duckling.number"];
                        $reactions.transition("/TravelRequest/AskStartDate");
                    } 
                    else if ($parseTree["pattern"]) {
                        $session.numberOfPeople = 1;
                        $reactions.transition("/TravelRequest/AskStartDate");
                        }
                
            state: DontKnow  
                intent: /незнаем
                script:
                    $session.numberOfPeople = "Не указано";
                    $reactions.transition("/TravelRequest/AskStartDate");
                
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
                    go!: /TravelRequest/AskNumberOfPeople/DontKnow

        state: AskStartDate
            a: Еще мне потребуется предполагаемая дата начала поездки. Пожалуйста, напишите ее.
            script:
                $session.stateCounterInARow = 0;
            
            state: Date
                q: * @duckling.date *
                script:
                
                    if ($parseTree["_duckling.date"]) {
                        $session.startDate = new Date($parseTree["_duckling.date"].year + "/"+ $parseTree["_duckling.date"].month + "/"+ $parseTree["_duckling.date"].day);
                    
                        var date = new Date();
                        var userDate = $session.startDate;
                        if (userDate.setHours(0,0,0,0) < date.setHours(0,0,0,0)) {
                            $reactions.transition("/TravelRequest/AskStartDate/LocalCatchAll");
                        }  else {
                            $reactions.transition("/TravelRequest/AskDuration");
                            }
                    }
                    
            state: DontKnow  
                intent: /незнаем
                script:
                    $session.startDate = "Не указано";
                    $reactions.transition("/TravelRequest/AskDuration");
                
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
                        $reactions.transition("/TravelRequest/AskStartDate/DontKnow");
                    
        state: AskDuration
            a: Также укажите, сколько дней будет длиться путешествие.
            script:
                $session.stateCounterInARow = 0;
       
            state: Number
                q: * @duckling.number *
                intent: /неделя
                script:
                    if ($parseTree["_duckling.number"] > 0) {
                        $session.countDays = $parseTree["_duckling.number"];
                        $session.endDate = addDays($session.startDate, $parseTree["_duckling.number"]);
                        $reactions.transition("/TravelRequest/AskServices");
                    } else if ($parseTree["pattern"]) {
                        $session.countDays = 7;
                        $session.endDate = addDays($session.startDate, 7);
                        $reactions.transition("/TravelRequest/AskServices");
                        } else {
                            $reactions.transition("/TravelRequest/AskDuration/LocalCatchAll");
                            }
                      
            state: DontKnow  
                intent: /незнаем
                script:
                    $session.endDate = "Не указано";
                    $reactions.transition("/TravelRequest/AskServices");
                    
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
                        $reactions.transition("/TravelRequest/AskDuration/DontKnow");
            
        state: AskServices
            a: Уточните, пожалуйста, какой пакет услуг вам интересен?
            script: 
                $session.stateCounterInARow = 0;
            buttons:
                "Эконом" -> /TravelRequest/AskServices/Package
                "Стандарт" -> /TravelRequest/AskServices/Package
                "VIP" -> /TravelRequest/AskServices/Package
            
            state: Package
                q: @Packages  
                script:
                    $session.services = $request.query;
                go!:  /TravelRequest/AskName
                
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
            
                go!:  /TravelRequest/AskServices
            
            state: Price
                intent: /prices
                script:
                    if ($parseTree._Packages) {
                        if ($session.numberOfPeople !== "Не указано") {
                            if ($session.endDate !== "Не указано") {
                                $session.personalPrice = $session.numberOfPeople * $parseTree._Packages.perDayOneMan*$session.countDays;
                                var answer = "При оформлении пакета услуг \""+$parseTree._Packages.name+"\" на поездку для " +
                                $session.numberOfPeople +" "+ $nlp.conform($nlp.inflect("человек","gent"), $session.numberOfPeople) +" стоимость составит "+numberWithCommas($session.personalPrice)+ " "+$nlp.conform("рубль", $session.personalPrice)+".";
                                $reactions.answer(answer);
                                }
                        } else {
                            var answer = "При оформлении пакета услуг \""+$parseTree._Packages.name+" стоимость составит "+numberWithCommas($parseTree._Packages.perDayOneMan)+ " рублей на одного человека.";
                            $reactions.answer(answer);
                            }
                    } else {
                        var pk1 = JSON.parse($caila.entitiesLookup("эконом", true).entities[0].value);
                        var pk2 = JSON.parse($caila.entitiesLookup("стандарт", true).entities[0].value);
                        var pk3 = JSON.parse($caila.entitiesLookup("vip", true).entities[0].value);
                    
                        var answer = "При формировании пакета услуг \""+ pk1.name+"\" стоимость составит "+ numberWithCommas(pk1.perDayOneMan) + " рублей на одного человека. Для пакета \""+ pk2.name+"\" - "+ numberWithCommas(pk2.perDayOneMan)+ ". А \""+ pk3.name+"\" будет стоить "+ numberWithCommas(pk3.perDayOneMan) +" за одного человека.";
                        $reactions.answer(answer);
                        }
                go!: /TravelRequest/AskServices
            
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
                            $reactions.buttons([{ text: "Эконом", transition: "/TravelRequest/AskServices/Package" },
                            {text: "Стандарт", transition: "/TravelRequest/AskServices/Package"},
                            {text: "VIP", transition: "/TravelRequest/AskServices/Package"}]);
                            }
                else:
                    script: 
                        $session.stateCounterInARow = 0;
                        var answer = "К сожалению, без выбора пакета заявка не может быть отправлена. Вы можете вернуться к её заполнению позже или связаться с нами по номеру 8 (812) 000-00-00.";
                        $reactions.answer(answer);
                        $reactions.transition("/SomethingElse");
            
        state: AskName
            script:
                $session.stateCounterInARow = 0;
            if: $client.name
                script:
                    $reactions.transition("/TravelRequest/AskPhone");
            else:
                script:
                    if ($context.session.lastState !== "/TravelRequest/AskName/LocalCatchAll"){
                        var answer = "С параметрами заявки почти закончили! Осталось указать контакты, чтобы менеджер смог связаться с вами.";
                        $reactions.answer(answer);
                        }
                
                    var answer = "Введите, пожалуйста, ваше имя.";
                    $reactions.answer(answer);
                
                    
            state: Name
                q: * @pymorphy.name *
                q: * меня зовут * * $Name *
                q: * зови меня * * $Name *
                q: * имя * * $Name *
                q: * ладно * * $Name *
                q: * я * * $Name *
                script:
               
                    if ($parseTree["_pymorphy.name"]) {
                        $client.name = capitalize($parseTree["_pymorphy.name"]);
                        } else if ($parseTree["pattern"] && ($parseTree["_Root"] !== "да")) {
                            $session.userName = capitalize($request.query);
                            } 
                    
                go!: /TravelRequest/AskPhone    
            
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
                    $reactions.transition("/TravelRequest/AskName");       
                
            state: ChoosenYes
                q: * да * || fromState = "/TravelRequest/UnusualName", onlyThisState = true
                q: * $Name *
                event: noMatch
                script:
                    $reactions.transition("/TravelRequest/AskName/Name");  
                
        state: AskPhone
            script:
                $session.stateCounterInARow = 0;
            if: $client.phone
                go!: /TravelRequest/AskComment
            else:
                a: Укажите номер телефона для связи.
                script:
                    $reactions.buttons({ text: "Поделиться контактом", request_contact: true })
                
            state: Phone
                event: telegramSendContact
                q: * @duckling.phone-number *
                q: * {мой номер * * @duckling.phone-number} *
                script:
                    if ($parseTree["_duckling.phone-number"]) {
                        $client.phone_number = $parseTree["_duckling.phone-number"];
                        $reactions.transition("/TravelRequest/AskComment");  
                        }
                    else {
                        $client.phone_number = $request.rawRequest.message.contact.phone_number;
                        $reactions.transition("/TravelRequest/AskComment");  
                        }
                    
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
                "Не нужно" -> /TravelRequest/AskComment/Disagree
            
            state: Comment
                event: noMatch
                script:
                    $session.userComment = $request.query;
                go!: /TravelRequest/Confirmation
                
            state: Disagree
                intent: /незнаем
                intent: /неХочуУказывать
                intent: /зачем
                q: * нет *
                script:
                    $session.userComment = "Не указано";
                go!: /TravelRequest/Confirmation
    
        state: Confirmation
            script:
                $session.stateCounterInARow = 0;
                $session.stateCounter = 0;
            
                moment.lang('ru');
                var isImportant = false;
                $temp.confirmation = "Среди важных критериев подбора вы выделили:";
            
                if ($session.country != "Не указано") {
                    $temp.confirmation += " \n- Страна пребывания - "+ $session.country;
                    isImportant = true;
                    }
            
                if ($session.numberOfPeople != "Не указано") {
                    $temp.confirmation += " \n- Количество людей в поездке - "+$session.numberOfPeople;
                    isImportant = true;
                    }
            
                if ($session.startDate != "Не указано") {
                    $temp.confirmation += " \n- Приблизительная дата начала поездки - "+ moment($session.startDate).format('LL');
                    isImportant = true;
                    }
           
                if ($session.endDate != "Не указано") {
                    $temp.confirmation += " \n- Приблизительная дата окончания поездки - "+ moment($session.endDate).format('LL');
                    isImportant = true;
                    }
            
                if ($session.services != "Не указано") {
                    $temp.confirmation += " \n- Желаемый пакет услулуг - "+ $session.services;
                    isImportant = true;
                    } 
                
                if ($session.userComment != "Не указано") {
                    $temp.confirmation += " \n- Комментарий для менеджера - \""+$session.userComment + "\"";
                    isImportant = true;
                    }  
            
                if ($session.personalPrice) {
                    $temp.confirmation += " \n- Примерная стоимость тура - "+numberWithCommas($session.personalPrice);
                    isImportant = true;
                    }
            
                $temp.confirmation += "."
            
                if (isImportant) {
                    $reactions.answer($temp.confirmation);
                    }
                
            a: Подскажите, вы готовы отправить заявку?
            buttons:
                "Да" -> /TravelRequest/Confirmation/Agree
                "Нет" -> /TravelRequest/Confirmation/Disagree
            
            state: Agree
                intent: /confirmationYes
                script: 
                    var message = "<i>Приветствую! \n"+
                    "Это автоматически отправленное ботом Артуром письмо о новой заявке на подбор тура. <ul>";
                
                    if ($client.name) {
                        message += "<li>Имя клиента: "+$client.name+"</li>";
                        } else {
                            message += "<li>Имя клиента: <i>"+$session.userName+"</li>";
                            }
                        
                    if ($client.phone_number != "Не указано") {
                        message += "<li>Телефон: "+ $client.phone_number+"</li>";
                        }
                
                    if ($session.country != "Не указано") {
                        message += "<li>Желаемая страна пребывания: "+ $session.country+"</li>";
                    }
            
                    if ($session.numberOfPeople != "Не указано") {
                        message += "<li>Количество людей в поездке: "+$session.numberOfPeople+"</li>";
                    }
            
                    if ($session.startDate != "Не указано") {
                        message += "<li>Приблизительная дата начала поездки: "+ moment($session.startDate).format('LL')+"</li>";
                    }
           
                    if ($session.endDate != "Не указано") {
                        message += "<li>Приблизительная дата окончания поездки: "+ moment($session.endDate).format('LL')+"</li>";
                    }
            
                    if ($session.services != "Не указано") {
                        message += "<li>Желаемый пакет услулуг: "+ $session.services+"</li>"
                    } 
                
                    if ($session.userComment != "Не указано") {
                        message += "<li>Комментарий клиента: \""+$session.userComment + "\""+"</li>";
                    }  
            
                    if ($session.personalPrice) {
                        message += "<li>Примерная стоимость тура: "+numberWithCommas($session.personalPrice)+"</li>";
                    }
            
                    message += "</ul></i>";
               
                    $session.userHasTour = true;
                
                    $temp.mailResult = sendEmail(message);
                
                    if ($temp.mailResult.status === "OK") {
                        $session.userHasTour = true;
                        $reactions.answer("Ваша заявка успешно отправлена! Как только наш менеджер выберет самые подходящие для вас варианты, он обязательно с вами свяжется.");
                        $reactions.transition("/GoodBye");    
                    }
                    else {
                        $reactions.transition("/TravelRequest/Confirmation/Agree/Error"); 
                    }
                
                state: Error
                    script:
                        $session.stateCounter ++;
                    
                    if: $session.stateCounter < 3
                        go!: /TravelRequest/Confirmation/Agree
                    else:
                        script:
                            $session.stateCounter = 0;
                            $session.stateCounterInARow = 0;
                            $session.country = null;
                            $session.numberOfPeople = null;
                            $session.startDate = null;
                            $session.endDate = null;
                            $session.services = null;
                            $session.userName = null;
                            $session.userComment = null;
                            $session.personalPrice = null;
                        
                            var answer = "К сожалению, произошла техническая ошибка при обращении к сервису хранения заявок. Пожалуйста, позвоните по вопросу подбора путевки нам в Just Tour по номеру 8 (812) 000-00-00.";
                            $reactions.answer(answer);
                            $reactions.transition("/SomethingElse");   
                        
            state: Disagree
                intent: /confirmationNo
                a: В таком случае, вы всегда можете вернуться к заполнению заявки повторно или связаться с нами по телефону 8 (812) 000-00-00.
                script:
                    $session.country = null;
                    $session.numberOfPeople = null;
                    $session.startDate = null;
                    $session.endDate = null;
                    $session.services = null;
                    $session.userName = null;
                    $session.userComment = null;
                    $session.personalPrice = null;
                go!: /SomethingElse
            
            state: LocalCatch || noContext = true
                event: noMatch
                script:
                    $session.stateCounterInARow ++
                
                if: $session.stateCounterInARow < 3
                    script:
                        var answers = ["Извините, не совсем понял вас. Хотите отправить эту заявку?",
                        "К сожалению, не смог понять вас. Отправляем эту заявку?"];
                        var randomAnswer = answers[$reactions.random(answers.length)];
                        $reactions.answer(randomAnswer);
                    
                        $reactions.buttons([{ text: "Да", transition: "/TravelRequest/Confirmation/Agree" },
                        {text: "Нет", transition: "/TravelRequest/Confirmation/Disagree"}])
                    
                else:
                    script: 
                        $session.stateCounterInARow = 0;
                        $session.country = null;
                        $session.numberOfPeople = null;
                        $session.startDate = null;
                        $session.endDate = null;
                        $session.services = null;
                        $session.userName = null;
                        $session.userComment = null;
                        $session.personalPrice = null;
                    
                        var answer = "К сожалению, так и не смог понять, что имелось в виду. Вы всегда можете вернуться к заполнению заявки повторно или связаться с нами по номеру 8 (812) 000-00-00.";
                        $reactions.answer(answer);
                        $reactions.transition("/SomethingElse");     
                
    state: SomethingElse  
        random:
            a: Хотите спросить что-то ещё?
            a: Могу ли я помочь чем-то ещё?
            a: Подскажите, у вас остались ещё вопросы?
        script:
            $session.stateCounter = 0;
            $session.stateCounterInARow = 0;
        buttons:
            "Узнать прогноз погоды" -> /WeatherForecast
            "Оформить заявку на подбор тура" -> /TravelRequest
                
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
                
    state: DontHaveQuestions
        q!: * вопросов нет *
        q!: * У меня больше нет вопросов *
        random:
            a: Вас понял!
            a: Хорошо!
            a: Понял!
        go!: /GoodBye    
              
    state: GoodBye
        intent!: /пока
        random:
            a: Всего доброго!
            a: Всего вам доброго!
            a: Всего доброго, до свидания!
    
    