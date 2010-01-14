
library(fingerprint)
library(rcdklibs)
.jinit(classpath=c('rcdk.jar', '/Users/guhar/src/rcdklibs/pkg/inst/cont/cdk.jar', '/Users/guhar/src/rcdklibs/pkg/inst/cont/jcp.jar'),
       parameters="-Djava.awt.headless=TRUE")

.jinit(classpath=c('rcdk.jar'))

source('pkg/R/rcdk.R')

source('pkg/R/visual.R')
source('pkg/R/io.R')
source('pkg/R/smiles.R')
source('pkg/R/props.R')
source('pkg/R/atoms.R')
source('pkg/R/bonds.R')
source('pkg/R/formula.R')
source('pkg/R/desc.R')
source('pkg/R/matching.R')

# check the class path
.jcall("java/lang/System","S","getProperty","java.class.path")

m <- parse.smiles('CCC')
get.smiles(m)
view.molecule.2d(m)

mfSet <- generate.formula(18.03383,charge=1,
                          elements=list(c("C",0,50),c("H",0,50),c("N",0,50)))


data(bpdata)
mols <- sapply(bpdata$SMILES, parse.smiles)

mols <- m
filename <- 'foo.sdf'
together<-TRUE
write.props<-TRUE
value <-.jcall('org/guha/rcdk/util/Misc', 'V', 'writeMoleculesInOneFile',
                   .jarray(mols,
                           contents.class = "org/openscience/cdk/interfaces/IAtomContainer"),
                   as.character(filename), as.integer(ifelse(write.props,1,0)))


f <- list.files('/home/rguha/src/datasets/DHFR/origdata/all/sdf', pattern=glob2rx("*.sdf"),
                full.names=TRUE)
##f <- c('data/set2/dhfr00001.sdf', 'data/set2/dhfr00002.sdf', 'data/set2/dhfr00003.sdf')
mols <- load.molecules('/home/rguha/big.sdf')
mols <- load.molecules(f, FALSE, FALSE)

.jcall(
       .jcall(mols[[1]], "Lorg/openscience/cdk/interfaces/IAtom;", "getAtom", as.integer(1)),
       "Ljava/lang/String;", "getAtomTypeName"
       )

sp <- get.smiles.parser()
smiles <- c('CCC', 'CCN', 'CCN(C)(C)', 'c1ccccc1Cc1ccccc1','C1CCC1CC(CN(C)(C))CC(=O)CC')
mols <- sapply(smiles, parse.smiles, parser=sp)

view.molecule.2d(mols, ncol=2)

df <- data.frame(x=c(1, 2), y=c('a','b'), z=c(TRUE,FALSE))
df <- as.data.frame(c('a'))
view.table(mols[5], df, cellx=200,celly=200)

s <- load.molecules('/Users/rguha/big.sdf')
view.table(mols[1:4], data.frame(x=runif(4), y=c('a','b','c','d')), cellx = 300, celly=200)
view.table(mols[1:4], data.frame(x=runif(4), y=rnorm(4)), cellx = 300, celly=200)

mfSet <- generate.formula(18.03383,charge=1)
for (i in mfSet) {
  print(i)
}


source('package/rcdk/R/desc.R')
dc <- get.desc.categories()
dn <- get.desc.names(type=dc[1])
d3 <- c(get.desc.names(type=dc[1]), get.desc.names(type=dc[2]))

descs <- eval.desc(mols, d3, verbose=TRUE)

fps <- lapply(mols, get.fingerprint, type='maccs')

set.property(mols[[2]], "prop1", "hello")
set.property(mols[[2]], "prop2", 23)
set.property(mols[[2]], "prop3", 122)
get.properties(mols[[2]])


formula <- get.formula('CHCl3', charge = 0) 
isotopes <- get.isotopes.pattern(formula,minAbund=0.1)


mfSet <- generate.formula(18.03383,charge=1,
                          elements=list(c("C",0,50),c("H",0,50),c("N",0,50)))
