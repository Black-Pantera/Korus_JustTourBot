require: slotfilling/slotFilling.sc
  module = sys.zb-common
theme: /

    state: Start
        q!: $regex</start>
        q!: старт
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
            q: $Name
            script:
                $jsapi.startSession();
                $client.name = $parseTree._Name.name;
            go!: /HowCanIHelpYou
            
            state: ErrorName
                event: noMatch
                a: Аожалуйста, введите корректное имя
                go!: /GetName
        
    state: HowCanIHelpYou
        random:
            a: Чем могу помочь?
            a: Что вас интересует?
            a: Подскажите, какой у вас вопрос?
            buttons:
                "Узнать прогноз погоды" -> /WeatherForecast
                "Оформить заявку на подбор тура" -> /OfferTour
               
    state: Match
        event!: match
        a: {{$context.intent.answer}}