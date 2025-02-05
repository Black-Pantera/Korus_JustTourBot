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

function GetDegree(g) {
    if (g < -4) return "градусов";
    if (g > 4) return "градусов";
    
    switch (g) {
        case 0:
            return "градусов";
            break;
        case -1:     
        case 1: 
            return "градус";
            break;
        case -2: 
        case 2: 
        case -3: 
        case 3: 
        case -4: 
        case 4: 
            return "градуса";
            break;
        default:
           return "градусов";
    }
}

function addDays(date, days) {
    var newDate = new Date(date.getTime() + days * 24 * 60 * 60 * 1000);
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

function queryNominatim(address){
    return $http.query(
        encodeURI("https://nominatim.openstreetmap.org/search?q="+address+"&limit=1&format=json")
    )
}