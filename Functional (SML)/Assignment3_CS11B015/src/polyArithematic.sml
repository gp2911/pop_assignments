
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

