require: slotfilling/slotFilling.sc
  module = sys.zb-common
theme: /

    state: Start
        q!: $regex</start>
        if: $client.name
            random:
                a: {{ capitalize($client.name) }}, здравствуйте! Артур из Just Tour на связи. Рад снова видеть вас в чате!
                a: {{ capitalize($client.name) }}, приветствую! На связи Артур из Just Tour, лучшей в мире туристической компании. Рад снова пообщаться с вами!
        else
            script:
                $client.name = "Lora";
            random:
                a: Здравствуйте! Меня зовут Артур, бот-помощник компании Just Tour. Расскажу все о погоде в городах мира и помогу с оформлением заявки на подбор тура.
                a: Приветствую вас! Я Артур, работаю виртуальным ассистентом в Just Tour, лучшем туристическом агентстве. Проинформирую вас о погоде в разных городах и соберу все необходимые данные для запроса на подбор путевки.
        go!: /HowCanIHelpYou
            
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