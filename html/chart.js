var chart = null

function show(pricesFile)
{
    loadPrices(pricesFile, function(response)
    {
        var data = JSON.parse('[' + response + ']')
		processResult(data)
    })
}

function loadPrices(file, callback)
{
    var xobj = new XMLHttpRequest()
    xobj.overrideMimeType('text/plain')
    xobj.open('GET', file, true)
    xobj.onreadystatechange = function()
    {
        if ((xobj.readyState == 4) && (xobj.status == '200'))
        {
            callback(xobj.responseText)
        }
    }
    xobj.send(null)
}

function priceHistory(json)
{
    const list = []

    for (var index in json)
    {
        var element = json[index]
        list.push([element.timestamp, element.price])
    }

    return list
}

function eventHistory(json, type)
{
    const list = []
    var counter = 1

    for (var index in json)
    {
        var element = json[index]

        if (element[type])
        {
	        list.push({
	            x: element.timestamp,
	            y: element.price,
	            title: type.toUpperCase() + ': ' + counter++,
	            text: JSON.stringify(element, null, 4)
	        })
        }
    }

    return list
}

function processResult(json)
{
    const prices = priceHistory(json)
    const buys = eventHistory(json, 'buy')
    const sells = eventHistory(json, 'sell')

    render(prices, buys, sells)
}

function render(prices, buys, sells)
{
    chart = Highcharts.stockChart('container', {

        chart: {
            zoomType: 'x',
            panning: true,
            panKey: 'shift'
        },

        tooltip: {
            style: {
                width: '200px'
            },
            valueDecimals: 8,
            shared: true
        },

        plotOptions:{
            series:{
                turboThreshold: 100000
            }
        },

        series: [
            {
                name: 'Price',
                data: prices,
                id: 'dataseries',
                color: '#e3e300'
            },
            {
                type: 'flags',
                data: buys,
                onSeries: 'dataseries',
                shape: 'squarepin',
                color: '#00db04'
            },
            {
                type: 'flags',
                data: sells,
                onSeries: 'dataseries',
                shape: 'squarepin',
                color: '#0088e3'
            }
        ]
    })
}