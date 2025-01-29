require: slotfilling/slotFilling.sc
  module = sys.zb-common
theme: /

    state: Start
        q!: $regex</start>
        q!: старт
        script:
            $context.session = {};
            $context.client = {};
            $session.stateCounterInARow = 0
            
        image: https://media.istockphoto.com/id/511095951/ru/%D1%84%D0%BE%D1%82%D0%BE/%D0%BE%D0%BD-%D0%B7%D0%B4%D0%B5%D1%81%D1%8C-%D1%87%D1%82%D0%BE%D0%B1%D1%8B-%D0%BF%D0%BE%D0%BC%D0%BE%D1%87%D1%8C.jpg?s=2048x2048&w=is&k=20&c=86_eS2vtvuPqNIFl04rO9yg1N7bv9yQMpqIrM0SNOH4=
        go!: /GetName
        
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
            
            state: GetName
                q: * *
                script:
                    log("///////// MY LOG "+toPrettyString($parseTree));
                    $client.name = $parseTree._Root;
                a: Будем знакомы, {{ capitalize($client.name) }}.
                go!: /HowCanIHelpYou
            
            state: ErrorName
                event: noMatch
                a: Пожалуйста, введите корректное имя
                go!: /GetName
        
    state: HowCanIHelpYou
        random:
            a: Чем могу помочь?
            a: Что вас интересует?
            a: Подскажите, какой у вас вопрос?
        buttons:
            "Узнать прогноз погоды" -> /WeatherForecast
            "Оформить заявку на подбор тура" -> /OfferTour
            
        state: LocalCatchAll || noContex = true
            event: noMatch
            script:
                $session.stateCounterInARow++
                
            if: $session.stateCounterInARow < 3
                random: 
                    a: Извините, не совсем понял. Пожалуйста, подскажите, могу ли я чем-то вам помочь?
                    a: К сожалению, не смог понять, что вы имеете в виду. Подскажите, что вас интересует?
            else:
                a: Кажется, этот вопрос не в моей компетенции. Но я постоянно учусь новому, и, надеюсь скоро научусь отвечать и на него.
                
                script: 
                    $session.stateCounterInARow = 0
                    
                go!: /SomethingElse
                
    state: DontHaveQuestions
        q!: * вопросов нет *
        q!: * нет *
        q!: * У меня больше нет вопросов *
        random:
            a: Вас понял!
            a: Хорошо!
            a: Понял!
        go!: /GoodBye        
          
    state: WeatherForecast
        a: Погода
          
    state: OfferTour
        a: Тур
              
    state: SomethingElse  
        random:
            a: Хотите спросить что-то ещё?
            a: Могу ли я помочь чем-то ещё?
            a: Подскажите, у вас остались ещё вопросы?
        buttons:
            "Узнать прогноз погоды" -> /WeatherForecast
            "Оформить заявку на подбор тура" -> /OfferTour
            
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
                    "Оформить заявку на подбор тура" -> /OfferTour
            else:
                a: Простите, так и не смог понять, что вы имели ввиду.
                go!: /GoodBye
              
    state: GoodBye
        random:
            a: Всего доброго!
            a: Всего вам доброго!
            a: Всего доброго, до свидания!
               
    state: Match
        event!: match
        a: {{$context.intent.answer}}