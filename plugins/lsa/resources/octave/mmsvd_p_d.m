function mmsvd(fname, nsigma)
%matrix market svd 
%
A = mmread(fname);
[U,S,V] = svds(A, nsigma);
mmwrite('U_pd.mm', U);
mmwrite('V_pd.mm', V);
S = diag(S);
mmwrite('S_pd.mm',S)

  



