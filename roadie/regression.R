args <- commandArgs(TRUE)
#x <- 50;
stdv = lapply(strsplit(args[1], ','), as.numeric)[[1]]
speed = lapply(strsplit(args[2], ','), as.numeric)[[1]]
x <- mean(speed)

if(length(stdv) > 6){
stdv = data.frame(stdv)
speed = data.frame(speed)
v = cbind(speed, stdv)
lm.r <- lm(v$stdv ~ v$speed + I(speed^2) + I(speed^3))
coefs = coef(lm.r)
b0 = coefs[1]
b1 = coefs[2]
b2 = coefs[3]
b3 = coefs[4]

finalValue = b0 + b1*(x^1) + b2*(x^2) + b3*(x^3)

b = c(b0,b1,b2,b3);
write(finalValue, file="data.txt")
}else{
write(-1, file="data.txt")
}

