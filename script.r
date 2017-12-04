#table=read.csv("/home/max/github/botcoin/data.csv", header=TRUE)
table=read.csv("/home/max/github/botcoin/logs/log_1512429652433.csv", header=TRUE)
attach(table)
#View(table)

#table=table[,1:5]

length=length(table[,1])
min=20000
max=0

for (i in 1:length(table))
{
  temp_min=min(table[,i])
  temp_max=max(table[,i])
  
  if (temp_min < min)
  {
    min=temp_min
  }
  
  if (temp_max > max)
  {
    max=temp_max
  }
}

colors=c(
  "dodgerblue3",
  "firebrick1"
)

colors=rainbow(length(table))

par(lwd=1)
plot(1:length, xaxt="n", type="n", ylim=c(min, max), xlab="", ylab="", las=1)
abline(h=c(seq(from=0, to=15000, by=10)), col="gray80")
legend(0, max, legend=colnames(table), col=colors, lty=1:1)

for (i in 1:length(table))
{
  lines(table[,i], type="l", col=colors[i])
}

