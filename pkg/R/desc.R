.get.desc.values <- function(dval, nexpected) {
  if (!inherits(dval, "jobjRef")) {
    if (is.na(dval)) return(NA)
  }

  if (!is.null(.jcall(dval, "Ljava/lang/Exception;", "getException"))) {
    return(rep(NA, nexpected))
  }
  
  nval <- numeric()
  result <- .jcall(dval, "Lorg/openscience/cdk/qsar/result/IDescriptorResult;", "getValue")
  methods <- .jmethods(result)

  if ("public double org.openscience.cdk.qsar.result.DoubleArrayResult.get(int)" %in% methods) {
    result <- .jcast(result, "org/openscience/cdk/qsar/result/DoubleArrayResult")
    len <- .jcall(result, "I", "length")
    for (i in 1:len) nval[i] <- .jcall(result, "D", "get", as.integer(i-1))
  } else if ("public int org.openscience.cdk.qsar.result.IntegerArrayResult.get(int)" %in% methods) {
    result <- .jcast(result, "org/openscience/cdk/qsar/result/IntegerArrayResult")    
    len <- .jcall(result, "I", "length")
    for (i in 1:len) nval[i] <- .jcall(result, "I", "get", as.integer(i-1))    
  }  else if ("public int org.openscience.cdk.qsar.result.IntegerResult.intValue()" %in% methods) {
    result <- .jcast(result, "org/openscience/cdk/qsar/result/IntegerResult")    
    nval <- .jcall(result, "I", "intValue")
  } else if ("public double org.openscience.cdk.qsar.result.DoubleResult.doubleValue()" %in% methods) {
    result <- .jcast(result, "org/openscience/cdk/qsar/result/DoubleResult")    
    nval <- .jcall(result, "D", "doubleValue")    
  }

  return(nval)
}


.get.desc.engine <- function(type = 'molecular') {
  if (!(type %in% c('molecular', 'atomic', 'bond'))) {
    stop('type must bond, molecular or atomic')
  }
  type <- match(type, c('atomic', 'bond', 'molecular'))
  dengine <- .jnew('org/openscience/cdk/qsar/DescriptorEngine', as.integer(type))
  attr(dengine, 'descType') <- type
  pkg <- c('org.openscience.cdk.qsar.descriptors.atomic',
           'org.openscience.cdk.qsar.descriptors.bond',
           'org.openscience.cdk.qsar.descriptors.molecular')[ type ]
  attr(dengine, 'descPkg') <- pkg
  dengine
}

.get.desc.all.classnames <- function(type = 'molecular') {
  dengine <- .get.desc.engine(type)
  type <- attr(dengine, "descType")
  pkg <- attr(dengine, "descPkg")
  cn <- .jcall(dengine, 'Ljava/util/List;', 'getDescriptorClassNames')
  size <- .jcall(cn, "I", "size")
  cnames <- list()
  for (i in 1:size)
    cnames[[i]] <- .jsimplify(.jcast(.jcall(cn, "Ljava/lang/Object;", "get", as.integer(i-1)), "java/lang/String"))
                                        #cnames <- gsub(paste(pkg, '.', sep='',collapse=''), '',  unlist(cnames))
  unique(unlist(cnames)  )
}


get.desc.names <- function(type = "all") {
  if (type == 'all') return(.get.desc.all.classnames())
  if (!(type %in% c('topological', 'geometrical', 'hybrid',
                    'constitutional', 'protein', 'electronic'))) {
    stop("Invalid descriptor category specified")
  }
  ret <- .jcall("org/guha/rcdk/descriptors/DescriptorUtilities", "[Ljava/lang/String;",
                "getDescriptorNamesByCategory", type)
  if ("org.openscience.cdk.qsar.descriptors.molecular.IPMolecularLearningDescriptor" %in% ret) {
    pos <- which(ret == "org.openscience.cdk.qsar.descriptors.molecular.IPMolecularLearningDescriptor")
    return(ret[-pos])
  } else {
    return(ret)
  }
}


get.desc.categories <- function() {
  cats <- .jcall("org/guha/rcdk/descriptors/DescriptorUtilities", "[Ljava/lang/String;",
                 "getDescriptorCategories");
  gsub("Descriptor", "", cats)
}

eval.desc <- function(molecules, which.desc, verbose = FALSE) {
  if (class(molecules) != 'list') {
    jclassAttr <- attr(molecules, "jclass")
    if (jclassAttr != "org/openscience/cdk/interfaces/IAtomContainer" &&
        jclassAttr != "org/openscience/cdk/interfaces/IMolecule") {
      stop("Must provide a list of molecule objects or a single molecule object")
    }
    molecules <- list(molecules)
  } else {
    jclassAttr <- lapply(molecules, attr, "jclass")
    if (any(jclassAttr != "org/openscience/cdk/interfaces/IAtomContainer" &&
            jclassAttr != "org/openscience/cdk/interfaces/IMolecule")) {
      stop("molecule must be an IAtomContainer")
    }
  }

  if (length(which.desc) == 1) {
    desc <- .jnew(which.desc)
    
    dnames <- .jcall(desc, "[Ljava/lang/String;", "getDescriptorNames")
    dnames <- gsub('-', '.', dnames)
    
    descvals <- lapply(molecules, function(a,b,c,d) {
      .jcall(b, "Lorg/openscience/cdk/qsar/DescriptorValue;", "calculate", a)
    }, b=desc)

    vals <- lapply(descvals, .get.desc.values, nexpected = length(dnames))
    vals <- data.frame(do.call('rbind', vals))
    names(vals) <- dnames 
    return(vals)
  } else {
    counter <- 1
    dl <- list()
    dnames <- c()
    for (desc in which.desc) {
      if (verbose) { cat("Processing ", gsub('org.openscience.cdk.qsar.descriptors.molecular.', '', desc)
                         , "\n") }
      desc <- .jnew(desc)

      dnames <- .jcall(desc, "[Ljava/lang/String;", "getDescriptorNames")
      dnames <- gsub('-', '.', dnames)
      
      descvals <- lapply(molecules, function(a) {
        .jcall(desc, "Lorg/openscience/cdk/qsar/DescriptorValue;", "calculate", a, check=FALSE)
      })
      vals <- lapply(descvals, .get.desc.values, nexpected = length(dnames))
      vals <- data.frame(do.call('rbind', vals))
      names(vals) <- dnames
      
      dl[[counter]] <- vals
      counter <- counter + 1
    }
    do.call('cbind', dl)
  }
}

get.atomic.desc.names <- function(type = "all") {
  if (type == 'all') return(.get.desc.all.classnames('atomic'))
  if (!(type %in% c('topological', 'geometrical', 'hybrid',
                    'constitutional', 'protein', 'electronic'))) {
    stop("Invalid descriptor category specified")
  }
  return(.jcall("org/guha/rcdk/descriptors/DescriptorUtilities", "[Ljava/lang/String;",
                "getDescriptorNamesByCategory", type))
}

eval.atomic.desc <- function(molecule, which.desc, verbose = FALSE) {
  if (attr(molecule, "jclass") != "org/openscience/cdk/interfaces/IAtomContainer") {
    stop("Must supply an IAtomContainer object")
  }

  if (length(which.desc) > 1) {
    counter <- 1
    dl <- list()
    for (desc in which.desc) {
      if (verbose) { cat("Processing ", gsub('org.openscience.cdk.qsar.descriptors.atomic.', '', desc)
                         , "\n") }
      desc <- .jnew(desc)
      atoms = get.atoms(molecule)
      descvals <- lapply(atoms, function(a) {
        dval <- .jcall(desc, "Lorg/openscience/cdk/qsar/DescriptorValue;", "calculate", a, molecule, check=FALSE)
        if (!is.null(e<-.jgetEx())) {
          print("Java exception was raised")
          .jclear()
          dval <- NA
        }
        return(dval)
      })
      vals <- lapply(descvals, .get.desc.values)
      vals <- data.frame(do.call('rbind', vals))

      if (inherits(descvals[[1]], "jobjRef")) {
        names(vals) <- .jcall(descvals[[1]], "[Ljava/lang/String;", "getNames")
      } else {
        names(vals) <- gsub('org.openscience.cdk.qsar.descriptors.atomic.', '', desc)
      }
      dl[[counter]] <- vals
      counter <- counter + 1
    }
    do.call('cbind', dl)
  }
}

get.tpsa <- function(molecule) {
  if (attr(molecule, "jclass") != "org/openscience/cdk/interfaces/IAtomContainer") {
    stop("Must supply an IAtomContainer object")
  }

  desc <- .jnew("org.openscience.cdk.qsar.descriptors.molecular.TPSADescriptor")
  descval <- .jcall(desc, "Lorg/openscience/cdk/qsar/DescriptorValue;", "calculate", molecule)
  value <- .get.desc.values(descval, 1)
  return(value)
}

get.tpsa <- function(molecule) {
  if (attr(molecule, "jclass") != "org/openscience/cdk/interfaces/IAtomContainer") {
    stop("Must supply an IAtomContainer object")
  }

  desc <- .jnew("org.openscience.cdk.qsar.descriptors.molecular.TPSADescriptor")
  descval <- .jcall(desc, "Lorg/openscience/cdk/qsar/DescriptorValue;", "calculate", molecule)
  value <- .get.desc.values(descval, 1)
  return(value)
}

get.alogp <- function(molecule) {
  if (attr(molecule, "jclass") != "org/openscience/cdk/interfaces/IAtomContainer") {
    stop("Must supply an IAtomContainer object")
  }

  desc <- .jnew("org.openscience.cdk.qsar.descriptors.molecular.ALOGPDescriptor")
  descval <- .jcall(desc, "Lorg/openscience/cdk/qsar/DescriptorValue;", "calculate", molecule)
  value <- .get.desc.values(descval, 3)
  return(value[1])
}

get.xlogp <- function(molecule) {
  if (attr(molecule, "jclass") != "org/openscience/cdk/interfaces/IAtomContainer") {
    stop("Must supply an IAtomContainer object")
  }

  desc <- .jnew("org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor")
  descval <- .jcall(desc, "Lorg/openscience/cdk/qsar/DescriptorValue;", "calculate", molecule)
  value <- .get.desc.values(descval, 3)
  return(value)
}
