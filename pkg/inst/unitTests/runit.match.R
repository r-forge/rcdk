test.match1 <- function()
{
  m <- parse.smiles('CCCCc1cccc(Cl)c1')
  q <- 'cCl'
  checkTrue(match(q,m))
}

test.match2 <- function()
{
  m <- parse.smiles('CCCCc1cccc(Cl)c1')
  q <- 'CCCCc'
  checkTrue(match(q,m))
}

test.match3 <- function()
{
  m1 <- parse.smiles('CCCCc1cccc(Cl)c1')
  m2 <- parse.smiles('CC(N)(N)CC=O')
  q <- '[CD2]'
  checkTrue(all(match(q,list(m1,m2))))
  checkEquals(2, length(match(q,list(m1,m2))))
}
