function getWeekNumber(d) {
    d = new Date(Date.UTC(d.getFullYear(), d.getMonth(), d.getDate()));
    d.setUTCDate(d.getUTCDate() + 4 - (d.getUTCDay()||7));
    var yearStart = new Date(Date.UTC(d.getUTCFullYear(),0,1));
    var weekNo = Math.ceil(( ( (d - yearStart) / 86400000) + 1)/7);
    return [d.getUTCFullYear(), weekNo];
}

function DatesDiff(date1, date2) {
   var diffTime = Math.abs(date2 - date1);
   var diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24)); 
   return diffDays;
}

function addDays(date, days) {
    var dt = new Date(date);
    var newDate = new Date(dt.getTime() + days * 24 * 60 * 60 * 1000);
    return newDate;
}

function userDatePassed(date1, date2){
    var userDate = date1.setHours(0,0,0,0);
    var date = date2.setHours(0,0,0,0);
    
    return userDate < date;
}

function getUserDate(dt){
    return new Date(dt.year + "/"+ dt.month + "/"+ dt.day);
}

function getCountryByCode(code){
    var country = null;
    if ($caila.entitiesLookup(code, true) != null) {
        if ($caila.entitiesLookup(code, true).entities.length) {
            var pk = JSON.parse($caila.entitiesLookup(code, true).entities[0].value);
            country = pk.name;
        } 
        else 
        {
            country = null;
        }
    }
    
    return country;
}

function openWeatherMapCurrent(units, lang, lat, lon){
    return $http.get("http://api.openweathermap.org/data/2.5/weather?APPID=${APPID}&units=${units}&lang=${lang}&lat=${lat}&lon=${lon}", {
            timeout: 10000,
            query:{
                APPID: $env.get("OPENWEATHER_API_KEY", "Переменная не найдена"), 
                units: units,
                lang: lang,
                lat: lat,
                lon: lon
            }
        });
}

function sendEmail(message) {
    return $mail.send({
                    smtpHost: $env.get("SMTP_HOST", "Переменная не найдена"), 
                    smtpPort: $env.get("SMTP_PORT", "Переменная не найдена"),
                    user: "larius77@mail.ru",
                    password: $env.get("EMAIL_PASSWORD", "Переменная не найдена"),
                    from: "Larius77@mail.ru",
                    to: ["Larius77@mail.ru"],
                    hiddenCopy: ["1993viktoria1993@mail.ru","allla.grgrn@gmail.com"],
                    subject: "Оформление тура",
                    content: message
                });
}

function numberWithCommas(x) {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

function confirmation() {
    var $session = $jsapi.context().session;
    
    var confirmation = "Среди важных критериев подбора вы выделили:";
    
    if ($session.country != "Не указано") {
        confirmation += " \n- Страна пребывания - "+ $session.country;
    }
            
    if ($session.numberOfPeople != "Не указано") {
        confirmation += " \n- Количество людей в поездке - "+$session.numberOfPeople;
    }
            
    if ($session.startDate != "Не указано") {
        confirmation += " \n- Приблизительная дата начала поездки - "+ moment($session.startDate).format('LL');
    }
           
    if ($session.endDate != "Не указано") {
        confirmation += " \n- Приблизительная дата окончания поездки - "+ moment($session.endDate).format('LL');
    }
            
    if ($session.services != "Не указано") {
        confirmation += " \n- Желаемый пакет услулуг - "+ $session.services;
    } 
                
    if ($session.userComment != "Не указано") {
        confirmation += " \n- Комментарий для менеджера - \""+$session.userComment + "\"";
    }  
            
    if ($session.personalPrice) {
        confirmation += " \n- Примерная стоимость тура - "+numberWithCommas($session.personalPrice);
    }
    
    confirmation += "."
    
    return confirmation;
}
