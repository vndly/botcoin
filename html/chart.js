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

function buysHistory(json)
{
    const list = []

    for (var index in json)
    {
        var element = json[index]

        if (element.buy)
        {
	        list.push({
	            type: 'buy',
	            x: element.timestamp,
	            y: element.price,
	            title: 'buy: ' + index,
	            text: 'yes'
	        })
        }
    }

    return list
}

function processResult(json)
{
    const prices = priceHistory(json)
    console.log(prices)
    const buys = buysHistory(json)
    console.log(buys)

    render(prices, buys)
}

function render(prices, buys)
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
                id: 'dataseries'
            },
            {
                type: 'flags',
                data: buys,
                onSeries: 'dataseries',
                shape: 'squarepin',
                color: '#00DB04',
                width: 30
            }
        ]
    })
}