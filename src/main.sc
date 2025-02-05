require: slotfilling/slotFilling.sc
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
        });

    state: Start
        q!: $regex</start>
        q!: старт
        script:
            //$context.session = {};
            //$context.client = {};
            $session.stateCounterInARow = 0
            $session.stateCounter = 0
            $session.stateCounterName = 0
            $session.stateCounterInARowCountry = 0
            $session.stateCounterInARowCity = 0
            $session.userDate = null
            $session.userCity = null;
            $session.lat = null;
            $session.lon = null;
            $session.country = null;
            
        image: https://media.istockphoto.com/id/511095951/ru/%D1%84%D0%BE%D1%82%D0%BE/%D0%BE%D0%BD-%D0%B7%D0%B4%D0%B5%D1%81%D1%8C-%D1%87%D1%82%D0%BE%D0%B1%D1%8B-%D0%BF%D0%BE%D0%BC%D0%BE%D1%87%D1%8C.jpg?s=2048x2048&w=is&k=20&c=86_eS2vtvuPqNIFl04rO9yg1N7bv9yQMpqIrM0SNOH4=
        go!: /GetName
        
    state: GlobalCatchAll || noContext = true
        event!: noMatch
        script:
            $session.stateCounterInARow++
                
        if: $session.stateCounterInARow < 3
            random: 
                a: Прошу прощения, не совсем вам понял. Попробуйте, пожалуйста, переформулировать ваш вопрос.
                a: Простите, не совсем понял. Что именно вас интересует?
                a: Простите, не получилось вас понять. Переформулируйте, пожалуйста.
                a: Не совсем понял вас. Пожалуйста, попробуйте задать вопрос по-другому.
        else:
            a: Кажется, этот вопрос не в моей компетенции. Но я постоянно учусь новому, и, надеюсь скоро научусь отвечать и на него.
                
            script: 
                $session.stateCounterInARow = 0
                    
            go!: /SomethingElse
            
    state: GetName
        if: $client.name
            random:
                a: {{ capitalize($client.name) }}, здравствуйте! Артур из Just Tour на связи. Рад снова видеть вас в чате!
                a: {{ capitalize($client.name) }}, приветствую! На связи Артур из Just Tour, лучшей в мире туристической компании. Рад снова пообщаться с вами!
            go!: /HowCanIHelpYou
        else
            go!: /GetName/InputName
            
            
        state: InputName
            random:
                a: Здравствуйте! Меня зовут Артур, бот-помощник компании Just Tour. Расскажу все о погоде в городах мира и помогу с оформлением заявки на подбор тура.
                a: Приветствую вас! Я Артур, работаю виртуальным ассистентом в Just Tour, лучшем туристическом агентстве. Проинформирую вас о погоде в разных городах и соберу все необходимые данные для запроса на подбор путевки.
            a: Как к вам лучше обращаться?
            
            state: SetName
                q: * @Names *
                q: * @pymorphy.name *
                script:
                    if ($parseTree._Names) {
                        $client.name = $parseTree._Names.name;
                        $reactions.transition("/HowCanIHelpYou");
                        }
                    
                    if ($parseTree["_pymorphy.name"]) {
                        $client.name = $parseTree["_pymorphy.name"];
                        $reactions.transition("/HowCanIHelpYou");
                        }
                    log("///////// MY LOG "+toPrettyString($parseTree));    
                    //$reactions.answer($client.name);
            
            state: ErrorName
                event: noMatch
                script:
                    $session.stateCounterName++
                
                if: $session.stateCounterName < 3
                    a: Пожалуйста, введите корректное имя
                    go!: /GetName/InputName
                else:
                    script: 
                        $session.stateCounterName = 0
                    a: Кажется, я не знаком с таким именем.
                    go!: /GoodBye
                
    state: HowCanIHelpYou
        random:
            a: Чем могу помочь?
            a: Что вас интересует?
            a: Подскажите, какой у вас вопрос?
            
        if: $request.channelType === "telegram"
            inlineButtons:
                { text: "Узнать прогноз погоды", callback_data: "/WeatherForecast" }
                { text: "Оформить заявку на подбор тура", callback_data: "/TravelRequest" }
        else:
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
            else:
                script: 
                    $session.stateCounterInARow = 0
                a: Кажется, этот вопрос не в моей компетенции. Но я постоянно учусь новому, и, надеюсь скоро научусь отвечать и на него.
                go!: /SomethingElse
           
    state: WeatherForecast
        intent!: /weather
        q!: * {@Cities * * @duckling.date} *
        script:
            log("///////// MY LOG "+toPrettyString($parseTree));
            
            if (($parseTree._Cities) && ($parseTree["_duckling.date"])) {
                $session.userCity = $parseTree._Cities.name;
                $session.lon = $parseTree._Cities.lon;
                $session.lat = $parseTree._Cities.lat;
                $session.country = $parseTree._Cities.country;   
                $session.userDate = new Date($parseTree["_duckling.date"].year + "/"+ $parseTree["_duckling.date"].month + "/"+ $parseTree["_duckling.date"].day);
                $reactions.transition("/CheсkDate");
                }
            else 
                if ($parseTree["_duckling.date"]) {
                    $session.userDate = new Date($parseTree["_duckling.date"].year + "/"+ $parseTree["_duckling.date"].month + "/"+ $parseTree["_duckling.date"].day);
                    $reactions.transition("/GetCity");
                    }
                else 
                    if ($parseTree._Cities) {
                        $session.userCity = $parseTree._Cities.name;
                        $session.lon = $parseTree._Cities.lon;
                        $session.lat = $parseTree._Cities.lat;
                        $session.country = $parseTree._Cities.country; 
                        
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
            q!: * @Cities *
            script:
                log("///////// MY LOG "+toPrettyString($parseTree));
                
                if ($parseTree._Cities) {
                    $session.userCity = $parseTree._Cities.name;
                    $session.lon = $parseTree._Cities.lon;
                    $session.lat = $parseTree._Cities.lat;
                    $session.country = $parseTree._Cities.country;                    
                    }
                    
                if ($parseTree["_duckling.date"])
                    $reactions.transition("/CheсkDate");
                else
                    $reactions.transition("/GetDate");
        
        state: LocalCatchAll || noContex = true
            event: noMatch
            script:
                $session.stateCounterInARowCity++
                
            if: $session.stateCounterInARowCity < 3
                random: 
                    a: Извините, не совсем понял вас. Напишите, пожалуйста, название города, чтобы я смог узнать прогноз погоды для него.
                    a: К сожалению, не понял вас. Укажите, пожалуйста, нужный вам город?
            else:
                a: Простите! Кажется, я пока не умею узнавать прогноз погоды с такими параметрами, но постараюсь поскорее научиться.
                
                script: 
                    $session.userDate = null
                    $session.stateCounterInARowCity = 0
                    
                go!: /SomethingElse
                
    state: GetDate
        random:
            a: На какую дату требуется прогноз?
            a: Прогноз погоды на какую дату вам нужен?
            
        state: UserDate
            q: * @duckling.date *
            script:
                log("///////// MY LOG "+toPrettyString($parseTree));
                
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
                    $session.stateCounterInARow = 0
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
                
                $reactions.transition("/ThisDayHasPassed");
                } 
                else if (DatesDiff(userDate, date) > 5) {
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
                a: У меня получилось уточнить: на {{ $temp.userFormatDate }} в {{capitalize($nlp.inflect($session.userCity, "loct"))}} температура воздуха составит {{ Math.floor($temp.response.data.main.temp)}} {{ GetDegree(Math.floor($temp.response.data.main.temp))}} по Цельсию.
                a: Смог узнать для вас прогноз: на {{ $temp.userFormatDate }} в {{capitalize($nlp.inflect($session.userCity, "loct"))}} будет {{Math.floor($temp.response.data.main.temp)}} {{ GetDegree(Math.floor($temp.response.data.main.temp))}} по Цельсию.
        else:
            a: У меня не получилось узнать погоду. Попробуйте ещё раз.
            script:
                $reactions.answer($temp.response);
                
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
        if: $session.country
            go!: /AskNumberOfPeople
        else:
            a: Подскажите, вы уже определились с страной прибытия?
            
        state: Agree
            q: * @Countries *
            q: * (да|ага|yes) *
            script: 
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
                    
            state: LocalCatchAll
                event: noMatch
                script:
                    $session.stateCounterInARowCountry++
                
                if: $session.stateCounterInARowCountry < 3
                    random:
                        a: Извините, не совсем понял вас. Подскажите, вы выбрали страну для путешествия?
                        a: К сожалению, не понял вас. Вы выбрали страну для поездки?
                    script:
                        $reactions.transition("/TravelRequest");
                else:
                    script:
                        $session.stateCounterInARowCountry = 0
                    a: Простите! Так и не получилось вас понять. Когда консультант получит заявку, он подберет варианты стран для вас. А теперь давайте перейдем к указанию оставшихся параметров.
                    go!: /AskNumberOfPeople
               
        state: Disagree
            q: * нет * 
            a: Понял вас. В таком случае, когда консультант получит заявку, он подберет варианты стран для вас. А теперь давайте перейдем к указанию оставшихся параметров.
            script:
                $session.country = "Не указано";  
            go!: /AskNumberOfPeople
                
                  
    state: AskNumberOfPeople
        a: Укажите количество человек, которые отправятся в путешествие.
        
        state: Number
            q: * @duckling.number *
            script:
                if ($parseTree["_duckling.number"] > 0) {
                    $session.numberOfPeople = $parseTree["_duckling.number"];
                    $reactions.transition("/AskStartDate");
                } else {
                    $reactions.transition("/AskNumberOfPeople/LocalCatchAll");
                    }
                
        state: DontKnow        
            script:
                $session.numberOfPeople = "Не указано";
                $reactions.transition("/AskStartDate");
                
        state: LocalCatchAll
            event: noMatch
            script:
                $session.stateCounterInARow ++
                
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
                     $reactions.transition("/AskNumberOfPeople/DontKnow");
            
    state: AskStartDate
        a: Еще мне потребуется предполагаемая дата начала поездки. Пожалуйста, напишите ее.
            
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
            
        if: $request.channelType === "telegram"
            inlineButtons:
                { text: "Узнать прогноз погоды", callback_data: "/WeatherForecast" }
                { text: "Оформить заявку на подбор тура", callback_data: "/TravelRequest" }
        else:
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
                    
                if: $request.channelType === "telegram"
                    inlineButtons:
                        { text: "Узнать прогноз погоды", callback_data: "/WeatherForecast" }
                        { text: "Оформить заявку на подбор тура", callback_data: "/TravelRequest" }
                else:
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
               
    