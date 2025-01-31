function getWeekNumber(d) {
    d = new Date(Date.UTC(d.getFullYear(), d.getMonth(), d.getDate()));
    d.setUTCDate(d.getUTCDate() + 4 - (d.getUTCDay()||7));
    var yearStart = new Date(Date.UTC(d.getUTCFullYear(),0,1));
    var weekNo = Math.ceil(( ( (d - yearStart) / 86400000) + 1)/7);
    return [d.getUTCFullYear(), weekNo];
}


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
