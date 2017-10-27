package uk.ac.ebi.mg.rwarbiter;

public interface TokenFactory<RT, WT, URT> {

    RT createReadToken();

    WT createWriteToken();

    URT createUpgradableReadToken();
}
