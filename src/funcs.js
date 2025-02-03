function getWeekNumber(d) {
    d = new Date(Date.UTC(d.getFullYear(), d.getMonth(), d.getDate()));
    d.setUTCDate(d.getUTCDate() + 4 - (d.getUTCDay()||7));
    var yearStart = new Date(Date.UTC(d.getUTCFullYear(),0,1));
    var weekNo = Math.ceil(( ( (d - yearStart) / 86400000) + 1)/7);
    return [d.getUTCFullYear(), weekNo];
}

function DatesDiff(date1, date2) {
   var diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24)); 
   return diffDays;
}

/*
function getForecast(lat,lon) {
    var settings = {
        "url": "https://api.openweathermap.org/data/2.5/forecast?lat=${lat}&lon=${lon}&appid=106ad0548ad7d7b7eb02682ec63886b4",
        "method": "GET",
        "timeout": 0,
    };

    $.ajax(settings).done(function (response) {
        return response;
    });
}
*/

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

function queryNominatim(address){
    return $http.query(
        encodeURI("https://nominatim.openstreetmap.org/search?q="+address+"&limit=1&format=json")
    )
}