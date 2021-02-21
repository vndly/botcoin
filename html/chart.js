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
        var data = json[index].data
        list.push([data.timestamp, data.value])
    }

    return list
}

function eventHistory(json, type)
{
    const list = []
    var counter = 1

    for (var index in json)
    {
        var data = json[index].data
        var events = json[index].events

        if (events && events[0].custom && (events[0].custom.type === type))
        {
            var custom = events[0].custom

	        list.push({
	            x: data.timestamp,
	            y: data.value,
	            title: type.toUpperCase() + ': ' + counter++,
	            text: eventSummary(custom)
	        })
        }
    }

    return list
}

function eventSummary(data)
{
	var result = ''

	for (var [key, value] of Object.entries(data))
	{
		if (key !== 'type')
		{
			if (result !== '')
			{
				result += '<br/>'
			}

	        result += `${key}: ${value.amount} ${value.asset}`
        }
    }

	return result
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