########################################################
##  set a cdkFormula function   

setClass("cdkFormula", representation(mass = "numeric",
                                      objectJ = "jobjRef",
                                      string = "character",
                                      charge = "numeric",
                                      isotopes = "matrix"),
         prototype(mass = 0,
                   objectJ = NULL,
                   string = character(0),
                   charge = 0,
                   isotopes = matrix(nrow = 0, ncol = 0))
         )

########################################################
##  create a cdkFormula function from the characters   

get.formula <- function(mf, charge=0) {
	
	manipulator <- .jnew("org/openscience/cdk/tools/manipulator/MolecularFormulaManipulator");
	if(!is.character(mf)) {
		stop("Must supply a Formula string");
	}else{
		dcob <- .jcall("org/openscience/cdk/DefaultChemObjectBuilder",
				"Lorg/openscience/cdk/DefaultChemObjectBuilder;",
				"getInstance");
		dcob <- .jcast(dcob, "org/openscience/cdk/interfaces/IChemObjectBuilder");
		molecularformula <- .jcall(dcob,"Lorg/openscience/cdk/interfaces/IMolecularFormula;","newMolecularFormula");
		molecularFormula <- .jcall(manipulator, "Lorg/openscience/cdk/interfaces/IMolecularFormula;","getMolecularFormula",mf,molecularformula,TRUE);
	}
	
	D <- .jnew("java/lang/Double", charge)
	.jcall(molecularFormula,"V","setCharge",D);
	object <- .cdkFormula.createObject(molecularFormula);
	return(object);
}

setMethod("show", "cdkFormula",
          function(object) {
            cat('cdkFormula: ',object@string,
                ', mass = ',object@mass, ', charge = ',
                object@charge,  '\n')
          })
########################################################
##  Set the charge to a cdkFormula function.
########################################################
get.mol2formula <- function(molecule, charge=0) {
	if(((attr(molecule, "jclass") != "org/openscience/cdk/interfaces/IMolecule") ||
	   (attr(molecule, "jclass") != "org/openscience/cdk/interfaces/IAtomContainer") )== FALSE) {
		stop("Must supply an IAtomContainer or IMolecule object")
	}
	if(attr(molecule, "jclass") == "org/openscience/cdk/interfaces/IMolecule")
		molecule <-.jcast(molecule, "org/openscience/cdk/interfaces/IAtomContainer")
	
	formulaJ <- .jcall('org/openscience/cdk/tools/manipulator/MolecularFormulaManipulator',
			           "Lorg/openscience/cdk/interfaces/IMolecularFormula;",
					   "getMolecularFormula",
					   molecule);
	
    # needs that all isotopes contain the properties
	string <- .cdkFormula.getString(formulaJ);
	objectF <- .cdkFormula.createFormulaObject();
	moleculaJT <- .jcall('org/openscience/cdk/tools/manipulator/MolecularFormulaManipulator', "Lorg/openscience/cdk/interfaces/IMolecularFormula;","getMolecularFormula",string,objectF,TRUE);
			   
	Do <- .jnew("java/lang/Double", charge)
	.jcall(moleculaJT,"V","setCharge",Do);	   

	formula <- .cdkFormula.createObject(moleculaJT)
	return(formula);
}
########################################################
##  Set the charge to a cdkFormula function.
########################################################
set.charge.formula <- function(formula,charge) {
  if (class(formula) != "cdkFormula")
    stop("Supplied object should be a cdkFormula Class")
  
  molecularFormula <- formula@objectJ;
  
  D <- .jnew("java/lang/Double", charge)
  .jcall(molecularFormula,"V","setCharge",D);
  
  formula@objectJ <- molecularFormula;
  formula@charge <- charge;
  
  return(formula)
}

########################################################
##  Validate a cdkFormula.
########################################################

isvalid.formula <- function(formula,rule=c("nitrogen","RDBE")){
  
	if (class(formula) != "cdkFormula")
    stop("Supplied object should be a cdkFormula Class")
  
  	molecularFormula <- formula@objectJ;
  
	for(i in 1:length(rule)){
	    ##Nitrogen Rule
		if(rule[i] == "nitrogen"){
			nRule <- .jnew("org/openscience/cdk/formula/rules/NitrogenRule");
			valid <- .jcall(nRule,"D","validate",molecularFormula);
			
			if(valid != 1.0){
			  return (FALSE)
			}
		}	  
		##RDBE Rule
		if(rule[i] == "RDBE"){
			rdbeRule <- .jnew("org/openscience/cdk/formula/rules/RDBERule");
			valid <- .jcall(rdbeRule,"D","validate",molecularFormula);
			  
			if(valid != 1.0){
			  return (FALSE)
			}
			else return(TRUE);
		}
	}
	return(TRUE);
}
	
#############################################################
##  Generate the isotope pattern given a formula class
#############################################################
get.isotopes.pattern <- function(formula,minAbund=0.1){
  
  if (class(formula) != "cdkFormula")
    stop("Supplied object should be a cdkFormula Class")
  
  molecularFormula <- formula@objectJ;
  
  isoGen <- .jnew("org/openscience/cdk/formula/IsotopePatternGenerator",as.double(minAbund));
  isotopeSet <- .jcall(isoGen,
                       "Lorg/openscience/cdk/interfaces/IMolecularFormulaSet;",
                       "getIsotopes",molecularFormula);
  massList <- .jcall(isoGen,"Ljava/util/List;","getMassDistribution",isotopeSet);
  abunList <- .jcall(isoGen,"Ljava/util/List;","getIsotopeDistribution",isotopeSet);
  size <- .jcall(massList,"I","size");
  
  ## create a matrix adding the mass and abundance of the isotope pattern
  iso.col <- c("mass","abund");
  
  massVSabun <- matrix(ncol=2,nrow=size);
  colnames(massVSabun)<-iso.col;
  for (i in 1:size) {
    massVSabun[i,1] <- .jsimplify(.jcast(.jcall(massList,
                                                "Ljava/lang/Object;", "get",
                                                as.integer(i-1)), "java/lang/Double"))
    massVSabun[i,2] <- .jsimplify(.jcast(.jcall(abunList,
                                                "Ljava/lang/Object;", "get",
                                                as.integer(i-1)), "java/lang/Double"))
  }
  return (massVSabun);
}

########################################################
##  Generate a list of possible formula objects given a mass and 
##  a mass tolerance.
########################################################

generate.formula <- function(mass, window=0.01, 
		elements=list(c("C",0,50),c("H",0,50),c("N",0,50),c("O",0,50),c("S",0,50)), 
		validation=FALSE, charge=0.0){
  
  builder <- .cdkFormula.createChemObject();
  mfTool <- .jnew("org/openscience/cdk/formula/MassToFormulaTool",builder);
  ruleList <-.jcall("org/guha/rcdk/formula/FormulaTools", "Ljava/util/List;", "createList")
  
  ## TOLERANCE RULE
  toleranceRule <- .jnew("org/openscience/cdk/formula/rules/ToleranceRangeRule");
  ruleG <- .jcast(toleranceRule, "org/openscience/cdk/formula/rules/IRule");
  D <- .jnew("java/lang/Double", window)
  paramsA <- .jarray(list(D,D))
  paramsB <- .jcastToArray(paramsA)
  .jcall(ruleG,"V","setParameters",paramsB);
  ruleList <-.jcall("org/guha/rcdk/formula/FormulaTools", "Ljava/util/List;", "addTo",ruleList,ruleG)
  
  
  ## ELEMENTS RULE
  elementRule <- .jnew("org/openscience/cdk/formula/rules/ElementRule");
  ruleG <- .jcast(elementRule, "org/openscience/cdk/formula/rules/IRule");
   
  chemObject <- .cdkFormula.createChemObject();
  range <- .jnew("org/openscience/cdk/formula/MolecularFormulaRange");
  ifac <- .jcall("org/openscience/cdk/config/IsotopeFactory",
          "Lorg/openscience/cdk/config/IsotopeFactory;",
          "getInstance",chemObject);
  
  for (i in 1:length(elements)) {
      isotope <- .jcall(ifac,"Lorg/openscience/cdk/interfaces/IIsotope;","getMajorIsotope", as.character( elements[[i]][1] ));
     .jcall(range, ,"addIsotope",isotope,as.integer( elements[[i]][2] ),as.integer( elements[[i]][3] ));
  }
   
  paramsA <- .jarray(list(range))
  paramsB <- .jcastToArray(paramsA)
  .jcall(ruleG,"V","setParameters",paramsB);
  
  ruleList <-.jcall("org/guha/rcdk/formula/FormulaTools", "Ljava/util/List;", "addTo",ruleList,ruleG)
   
  ## Setting the rules int FormulaTools
  .jcall(mfTool,"V","setRestrictions",ruleList);
  
  mfSet <- .jcall(mfTool,"Lorg/openscience/cdk/interfaces/IMolecularFormulaSet;","generate",mass);
  sizeSet <- .jcall(mfSet,"I","size");
  ecList <- list();
  count = 1;
  
  for (i in 1:sizeSet) {
    mf = .jcall(mfSet,
      "Lorg/openscience/cdk/interfaces/IMolecularFormula;",
      "getMolecularFormula",
      as.integer(i-1));

	D <- .jnew("java/lang/Double", charge)
	.jcall(mf,"V","setCharge",D);
	object <- .cdkFormula.createObject(mf);
    
    isValid = TRUE;
    if(validation==TRUE)
      isValid = isvalid.formula(object);
    
    if(isValid==TRUE){ ## if it's true add to the list
      ecList[count] = object;
      count = count+1;
    }
  }
  ecList
}


#############################################################
##  Intern functions: Creating object
#############################################################

.cdkFormula.createChemObject <- function(){
  dcob <- .jcall("org/openscience/cdk/DefaultChemObjectBuilder",
                 "Lorg/openscience/cdk/DefaultChemObjectBuilder;",
                 "getInstance");
  dcob <- .jcast(dcob, "org/openscience/cdk/interfaces/IChemObjectBuilder");
  
}
.cdkFormula.createFormulaObject <- function(){
	dcob <- .jcall("org/openscience/cdk/DefaultChemObjectBuilder",
			"Lorg/openscience/cdk/DefaultChemObjectBuilder;",
			"getInstance");
	dcob <- .jcast(dcob, "org/openscience/cdk/interfaces/IChemObjectBuilder");
	cfob <- .jcall(dcob,"Lorg/openscience/cdk/interfaces/IMolecularFormula;","newMolecularFormula");
}

#############################################################
# extract the molecular formula string form the java object
#############################################################
.cdkFormula.getString <- function(molecularFormula) {
  
  if (attr(molecularFormula, "jclass") != 'org/openscience/cdk/interfaces/IMolecularFormula') {
    stop("Supplied object should be a Java reference to an IMolecularFormula")
  }
  formula <- .jcall('org/openscience/cdk/tools/manipulator/MolecularFormulaManipulator',
                    'S', 'getString', molecularFormula)
}

#############################################################
# create a formula class from the molecularFormula java object
#############################################################
.cdkFormula.createObject <- function(molecularformula){
	
	object <-new("cdkFormula")
	
	object@objectJ <- molecularformula;
	iterable <- .jcall(molecularformula,"Ljava/lang/Iterable;","isotopes"); 
	isoIter <- .jcall(iterable,"Ljava/util/Iterator;","iterator");
	size <- .jcall(molecularformula,"I","getIsotopeCount");
	isotopeList = matrix(ncol=3,nrow=size);
	colnames(isotopeList) <- c("isoto","number","mass");
	for(i in 1:size){
		isotope = .jcast(.jcall(isoIter,"Ljava/lang/Object;","next"), "org/openscience/cdk/interfaces/IIsotope");
		isotopeList[i,1] <- .jcall(isotope,"S","getSymbol");
		isotopeList[i,2] <- .jcall(molecularformula,"I","getIsotopeCount",isotope);
		ch <- .jcall(isotope,"Ljava/lang/Double;","getExactMass");
		isotopeList[i,3] <- .jcall(ch,"D","doubleValue");
	}
	
	object@string <- .cdkFormula.getString(molecularformula);
	manipulator <- .jnew("org/openscience/cdk/tools/manipulator/MolecularFormulaManipulator");
	cMass <- .jcall(manipulator,"D","getTotalExactMass",molecularformula);
	object@mass <- cMass;
	chargeDO <- .jcall(molecularformula,"Ljava/lang/Double;","getCharge");
	charge <- .jcall(chargeDO,"D","doubleValue");
	object <- set.charge.formula(object,charge)
	object@isotopes <- isotopeList;
	
	return(object);
}