(* get minimum power of the poly *)
fun getMinPower(k:Poly) = 
    if null k then 0
    else if null(tl k) then #power(hd k)
    else if( #power(hd k) < getMinPower(tl k)) then #power(hd k)
    else getMinPower(tl k);


(* get degree of the poly *)
fun getMaxPower(k:Poly) = 
    if null k then 0
    else if( #power(hd(k)) > getMaxPower(tl(k)) andalso Real.!=( #coeff(hd(k)) ,0.0) ) then #power(hd(k))
        else getMaxPower(tl(k));



(* remove term with power i from p *)
fun removeByPower(p : Poly,i)=
    if null p then []
    else if(#power(hd(p)) = i) then tl(p)
    else hd(p)::removeByPower(tl(p), i);

(* does poly have a term with power i? *)
fun termExists(p : Poly, i: int) = 
    if null p then false
    else if i > getMaxPower(p) then false
    else if(#power(hd(p)) = i) then true
    else termExists(tl(p), i);

(* get term pf poly with power i *)
fun getTerm(p : Poly, i : int)=
    if null p then {coeff = 0.0, power = i}
    else if not(termExists(p,i)) then {coeff = 0.0, power = i}
    else if i > getMaxPower(p) then {coeff=0.0, power=i}
    else if(#power(hd(p)) = i) then hd(p)
    else getTerm(tl(p), i);

(* pad zeroes in the poly *)
fun padZeroes(p : Poly, i : int) = 
    if i<0 then []
    else getTerm(p,i)::padZeroes(p,i-1);

(* remove zero terms from poly *)
fun purgeZeroes(p : Poly) = 
    if null p then []
    else if (Real.==( #coeff(hd(p)), 0.0 )) then purgeZeroes(tl(p))
    else hd(p)::purgeZeroes(tl(p));

(* get coefficient of power c in polynomial p *)
fun getCoeffOf(c:int , k:Poly) = 
    let
         val p = padZeroes(k, getMaxPower(k))
     in
        if null p then 0.0
        else if ( (#power (hd(p))) = c) then #coeff(hd(p))
        else getCoeffOf(c, tl(p))
     end ;


(* division of terms *)
fun termDivide(p : Term, q: Term) = 
    let
        val coeff1 = #coeff(p)
        val coeff2 = #coeff(q)
        val pow1 = #power(p)
        val pow2 = #power(q)
        val newCoeff = Real./(coeff1,coeff2);
        val newPow = pow1-pow2;
    in
        {coeff = newCoeff, power = newPow}
    end;

(* Add two terms with same power *)
fun addCoeffs(p : Term , q : Term) = 
    let
        val coeff1 = #coeff(p)
        val coeff2 = #coeff(q)
        val newTerm = {coeff = coeff1+coeff2, power = #power(p)}
    in
        newTerm
    end;

(* add 2 polys expressed in term format *)
fun addPolyByTerm(p : Poly, q : Poly) = 
    if null p then q
    else if null q then p
    else if #power(hd(p)) > #power(hd(q)) then hd(p)::addPolyByTerm(tl(p), q) 
    else if #power(hd(q)) > #power(hd(p)) then hd(q)::addPolyByTerm(p, tl(q))
    else addCoeffs(hd(p), hd(q))::addPolyByTerm(tl(p), tl(q));

(* multiply a poly by a term *)
fun mulTerm(p : Term, q : Poly) = 
    if null q then []
    else {coeff = #coeff(p) * #coeff(hd(q)), power = #power(p) + #power(hd(q))} :: mulTerm(p, tl(q));

(* multiply 2 polys expressed in term format *)
fun mulPolyByTerm(p:Poly, q:Poly) = 
    if null p then []
    else if null q then []
    else addPolyByTerm(mulTerm(hd(p), q), mulPolyByTerm(tl(p),q));

(* subtract 2 polys expressed in term format *)
fun subPolyByTerm( p : Poly, q : Poly) = 
    addPolyByTerm(p, mulPolyByTerm(q, parsePoly("-1")));

(* divide 2 polys expressed in term format *)
fun divPolyByTerm( p : Poly, q: Poly) = 
    let
        type soln = { quot : Poly, rem : Poly }
    in
        if null p then {quot = {coeff = 0.0, power = 0}::[], rem = parsePoly("0.0")}
        else if(getMaxPower(p) < getMaxPower(q)) then {quot = {coeff = 0.0, power = 0}::[], rem = purgeZeroes(p)}
        else 
            let
                val multFactor = termDivide(hd(p), hd(q))
                val polyToSub = mulTerm(multFactor,q)
                val polyafterSub = subPolyByTerm(p, padZeroes(polyToSub, getMaxPower(p)))
                val nextStep = divPolyByTerm(purgeZeroes(polyafterSub), q)
                val thisStepQuot : Poly = multFactor::[]
            in
                {quot = (addPolyByTerm(#quot(nextStep), thisStepQuot)), rem = (#rem(nextStep))}
            end
    end;



(* intergrate a term *)
fun integrateTerm(p : Term) = 
    let
        val currTerm = p
        val coeff = #coeff(currTerm)
        val pow = #power(currTerm)
        val newPow = pow+1
        val newCoeff = Real./(coeff, Real.fromInt(newPow))
    in
        {coeff = newCoeff, power = newPow}
    end

(* intergrarte a poly expressed in term format *)
fun integratePolyByTerm(p: Poly) = 
    if null p then {coeff = 0.0, power = 0}::[]
    else integrateTerm(hd(p))::integratePolyByTerm(tl(p));

(* find ith power of a poly expressed in term format *)
fun powPolyByTerm(p : Poly, i : int) = 
    if i = 1 then p
    else mulPolyByTerm(p, powPolyByTerm(p,i-1));
fun polyEquals(p : Poly, q : Poly) = 
    if ((null p) andalso (null q)) then
        true
    else if ((null p) orelse (null q))then
        false
    else if( not(getMaxPower(p) = getMaxPower(q))) then 
        false
    else 
        let
            val t1 = getTerm(p, getMaxPower(p))
            val t2 = getTerm(q, getMaxPower(q))
            val p1 = removeByPower(p, getMaxPower(p))
            val q1 = removeByPower(q, getMaxPower(q))
        in
            if( Real.==(#coeff(t1), #coeff(t2)) ) then
                polyEquals(p1,q1)
            else false
        end;

fun polyMember(k : Poly, l : Poly list) = 
    if(null l) then false
    else if( polyEquals(k,hd l)) then true
    else if( polyEquals( #rem(divPolyByTerm(hd(l), k)), parsePoly("0.0"))) then true
    else polyMember(k, tl(l));

fun getCommonMembers(p : Poly list, q : Poly list, r : Poly list) =
    if((null p) orelse (null q)) then r 
    else if(polyMember(hd(p), q) andalso not(polyMember(hd(p),r))) then
        hd(p)::getCommonMembers(tl(p), q, hd(p)::r)
    else getCommonMembers(tl(p), q, r);





(* remove common variable from poly terms *)
fun removeVar(p : Poly) =
    let
        val pow = getMinPower(p)
        val factor = if pow = 0 then []
                     else parsePoly("x^"^Int.toString(pow))
        val residue = if null factor then p::[]
                      else #quot(divPolyByTerm(p, factor))::[]
    in
        if null factor then p::[]
        else factor::residue
    end;

(* make coeff of highest power 1 *)
fun makeNomial(p: Poly) =
    let
         val const = #coeff( hd p)
         val term = { coeff = const, power = 0}
         val q = parsePoly(Real.toString(const))
     in
         purgeZeroes(#quot(divPolyByTerm(p, q)))
     end;

(* find p(k) by sbstituting x=k *)
fun substitute(x : real, p : Poly) = 
    if null p then 0.0
    else
        let 
            val term = hd(p)
        in
            roundToDecimalPlaces(#coeff(term)*Math.pow(x, Real.fromInt(#power(term))) + substitute(x, tl p))
        end;

(* get constant term *)
fun getConstant( p : Poly) = getCoeffOf(0,p);

fun substAndCheck(p : Poly, l : real list) = 
    if(null l) then []
    else if(Real.==(substitute(hd(l), p), 0.0)) then
        hd(l)::substAndCheck(p, tl(l) )
    else
        substAndCheck(p, tl(l) );
