test.set.props <- function() {
  m <- parse.smiles("CCCC")
  set.property(m, "foo", "bar")
  checkEquals(get.property(m,"foo"), "bar")
}

test.get.properties <- function() {
  m <- parse.smiles("CCCC")
  set.property(m, "foo", "bar")
  set.property(m, "baz", 1.23)  
  props <- get.properties(m)
  checkEquals(length(props), 2)
  checkTrue(all(names(props) == c('foo','baz')))
  checkEquals(props[[1]],'bar')
  checkEquals(props[[2]],1.23)  
}
