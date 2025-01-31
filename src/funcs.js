function getWeekNumber(d) {
    d = new Date(Date.UTC(d.getFullYear(), d.getMonth(), d.getDate()));
    d.setUTCDate(d.getUTCDate() + 4 - (d.getUTCDay()||7));
    var yearStart = new Date(Date.UTC(d.getUTCFullYear(),0,1));
    var weekNo = Math.ceil(( ( (d - yearStart) / 86400000) + 1)/7);
    return [d.getUTCFullYear(), weekNo];
}

function getForecast(lat,lon, date) {
    return $http.query("https://api.stormglass.io/v2/weather/point?lat=${lat}&lng={lon}&start=${start}&params=${params}", {
        method: "GET",
        timeout: 10000,
    })
    
}