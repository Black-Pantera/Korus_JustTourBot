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


function openWeatherMapCurrent(units, lang, lat, lon){
    return $http.query("http://api.openweathermap.org/data/2.5/weather?APPID=${APPID}&units=${units}&lang=${lang}&lat=${lat}&lon=${lon}", {
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
                    smtpHost: "smtp.mail.ru",
                    smtpPort: "465",
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

