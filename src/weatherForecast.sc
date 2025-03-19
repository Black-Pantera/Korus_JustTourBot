require: slotfilling/slotFilling.sc
  module = sys.zb-common
require: city/city.sc
    module = sys.zb-common  
require: name/name.sc
    module = sys.zb-common
require: dateTime/moment.min.js
    module = sys.zb-common    
  
require: funcs.js

theme: /

    state: WeatherForecast
        intent: /weather
        q!: * погода * {$City * * @duckling.date} *
        q!: * погода * $City *
        q!: * будет * @duckling.date * в * $City * (дождь|солнечно|пасмурно) *
        script:
            $session.stateCounter = 0;
            
            if ($parseTree._City && $parseTree["_duckling.date"]) {
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
                if ($parseTree["_duckling.date"] || $parseTree["_Date"]) 
                {
                    if ($parseTree["_duckling.date"])
                        $session.userDate = new Date($parseTree["_duckling.date"].year + "/"+ $parseTree["_duckling.date"].month + "/"+ $parseTree["_duckling.date"].day);
                    
                    if ($parseTree["_Date"])
                        $session.userDate = new Date($parseTree["_Date"].year + "/"+ $parseTree["_Date"].month + "/"+ $parseTree["_Date"].day);  
                    
                    $reactions.transition("/WeatherForecast/GetCity");
                } 
                else 
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
                        
                        $reactions.transition("/WeatherForecast/GetDate");
                    }
                    else 
                    {
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
                var userDate = new Date($session.userDate);
            
                if (userDatePassed(userDate, date)) {
                    
                    $reactions.transition("/WeatherForecast/ThisDayHasPassed");
                    } 
                    else if (DatesDiff(userDate, date) > 5) {
                        
                        $reactions.transition("/WeatherForecast/ThisDayIsNotComingSoon");
                        } else { 
                            
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
                intent: /somethingElseForWeather
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
                q: * $yesWant * || fromState = "/WeatherForecast/SomethingElseForWeather/AnotherOne", onlyThisState = true
                script:
                    $session.userCity = null;
                    $session.lon = null;
                    $session.lat = null;
                    $session.country = null;   
                    $session.userDate = null;
                go!: /HowCanIHelpYou
                
            state: DisAgree
                q: * $noWant * || fromState = "/WeatherForecast/SomethingElseForWeather/AnotherOne", onlyThisState = true
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
            q: * $yesWant * || toState = "/TravelRequest", onlyThisState = true
            q: * $somethingElseForWeather * || toState = "/WeatherForecast/SomethingElseForWeather/AnotherOne" , onlyThisState = true 
            
            state: OfferTourYes
                q: * @duckling.date * || fromState = "/WeatherForecast/OfferTour", onlyThisState = true || toState = "/WeatherForecast/SomethingElseForWeather/AnotherOne"
                
                
            state: Disagree 
                q: * $noWant * || fromState = "/WeatherForecast/OfferTour", onlyThisState = true
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
                    q: * $yesWant * || fromState = "/WeatherForecast/OfferTour/Disagree", onlyThisState = true
                    go!: /WeatherForecast
                
                state: DisagreeNo
                    q: * $noWant * || fromState = "/WeatherForecast/OfferTour/Disagree", onlyThisState = true
                    go!: /SomethingElse
                
                    state: LocalCatchAll || noContext = true 
                        event: noMatch || fromState = "/WeatherForecast/OfferTour/Disagree", onlyThisState = true
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