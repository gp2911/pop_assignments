(* get the next split point : + or - *)
fun searchSplit( s : string, i : int) = 
    if(String.size(s) = 1) then String.size(s)
    else if (String.sub(s,i) = #"+") then i
    else if (String.sub(s,i) = #"-") then i
    else if (i < String.size(s)-1) then searchSplit(s, i+1)
    else String.size(s);

(* get the next occurance of '*' in the string *)
fun searchMul(s : string, i : int)=
    if (String.sub(s,i) = #"*") then i
    else if (i < String.size(s)-1) then searchMul(s, i+1)
    else String.size(s);

(* get the next occurance of '^' in  the string *)
fun searchPow(s : string, i : int)=
    if (String.sub(s,i) = #"^") then i
    else if (i < String.size(s)-1) then searchPow(s, i+1)
    else String.size(s);

(* get coeff of a term expresses as a String *)
fun getCoeff(z : string) = 
    if (searchMul(z,0) = String.size(z)) then
        if(String.size(z)>1) then
            if(Char.isDigit(String.sub(z,1)) orelse (String.sub(z,1) = #"."))  
                then Option.getOpt(Real.fromString(z), 0.0) 
            else 1.0
        else
            if(Char.isDigit(String.sub(z,0)))  then Option.getOpt(Real.fromString(z), 0.0) 
            else 1.0
    else Option.getOpt(Real.fromString(String.substring(z,0,searchMul(z,0))), 0.0);

(* get power of a term expressed as a string *)
fun getPow(s: string) =
    if (searchPow(s,0) = String.size(s)) then 
        if(String.size(s)>1) then(*Option.getOpt(Real.fromString(String.concat([String.str(String.sub(s,0)),"1.0"])), 0.0) *)
            if Char.isDigit(String.sub(s,String.size(s)-1) )   then 0 
            else 1
        else
            if(Char.isDigit(String.sub(s,0)))  then 0
            else 1
    else Option.getOpt(Int.fromString(String.extract(s, searchPow(s,0)+1, NONE)),0);

(* convert a given string into a term *)
fun createTerm(s:string) = {coeff = getCoeff(s), power = getPow(s)};

(* remove a term from a polynomial strng *)
fun removeTerm(s : string) = 
     String.extract(s,searchSplit(s,1), NONE);

(* parse a given string into a Polynomial *)
fun parsePoly(s : string) = 
    if searchSplit(s,1)  = String.size(s) then createTerm(s)::[]
    else createTerm(String.substring(s, 0, searchSplit(s,1))) :: parsePoly(removeTerm(s));

fun powToString(t : Term, c : char)=
    let
        val pow = #power(t)
    in
        if pow = 1 then String.str( #"*" ) ^ String.str(c)
        else String.str(c)^String.str( #"^" )^Int.toString(pow)
    end;
fun coeffToString(t:Term)=
    let
        val coeff = #coeff(t)
    in
        (*if(Real.==(coeff ,1.0)) then ""
        else *)Real.toString(Real.abs(coeff))
    end

fun termToString(t : Term, c : char) = 
    if #power(t) = 0 andalso #coeff(t) < 0.0 then Char.toString(#"-")^coeffToString(t)
    else if #power(t) = 0 andalso #coeff(t) > 0.0 then Char.toString(#"+")^coeffToString(t)
    else if #power(t)>0 andalso #coeff(t) < 0.0 then Char.toString(#"-") ^ coeffToString(t) ^ powToString(t,c)
    else Char.toString(#"+")^coeffToString(t) ^ powToString(t,c); 

(* convert a polynomial to a printable string with leading + *)
fun polyToString1( p : Poly) = 
    let
        val stringSet = if null p then ""
                        else termToString(hd(p), #"x")^polyToString1(tl(p))
        (*val str = String.concat(stringSet)*)
    in
        stringSet
    end;


(* convert a polynomial to a printable string without leading + *)
fun polyToString( p : Poly) = 
    let
        val stringSet = if null p then ""
                        else termToString(hd(p), #"x")^polyToString1(tl(p))
        (*val str = String.concat(stringSet)*)
    in
        if stringSet = "" then stringSet
        else if String.sub(stringSet,0) = #"+" then String.extract(stringSet, 1, NONE)
        else stringSet
    end;
