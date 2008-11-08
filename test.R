
library(rJava)
library(fingerprint)
library(rcdklibs)
.jinit(classpath=c('rcdk.jar'))
source('package/rcdk/R/rcdk.R')

source('package/rcdk/R/visual.R')
source('package/rcdk/R/io.R')
source('package/rcdk/R/smiles.R')
source('package/rcdk/R/props.R')
source('package/rcdk/R/atoms.R')
source('package/rcdk/R/bonds.R')
source('package/rcdk/R/formula.R')
source('package/rcdk/R/desc.R')

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
