function mmsvd(fname, nsigma)
%matrix market svd 
%
A = mmread(fname);
[U,S,V] = svds(A, nsigma);
mmwrite('U.mm', U);
mmwrite('V.mm', V);
S = diag(S);
mmwrite('S.mm',S)

  



