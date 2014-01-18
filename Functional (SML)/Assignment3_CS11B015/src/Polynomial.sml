(* inport necessary stuff from other files *)

use "POP-CS11B015/Assmt3/Assignment3_CS11B015/src/Defs.sml";
use "POP-CS11B015/Assmt3/Assignment3_CS11B015/src/io.sml";
use "POP-CS11B015/Assmt3/Assignment3_CS11B015/src/polyStringInterface.sml";
use "POP-CS11B015/Assmt3/Assignment3_CS11B015/src/realManip.sml";
use "POP-CS11B015/Assmt3/Assignment3_CS11B015/src/polyManip.sml";


(**  
 *   Add two polynomilas expressed in String form 
 *   Input : String, String
 *   Output: String          
**)
fun addPoly(x : string, y:string) = 
    let
        val p = parsePoly(x)
        val q = parsePoly(y)
    in
        polyToString(purgeZeroes(addPolyByTerm(p,q)))
    end;

(**  
 *   Subtract two polynomilas expressed in String form 
 *   Input : String, String
 *   Output: String          
**)
fun subPoly(x : string, y: string) =
    let 
        val p = parsePoly(x)
        val q = parsePoly(y)
    in
        polyToString(purgeZeroes(subPolyByTerm(p,q)))
    end;

(**  
 *   Multiply to polynomilas expressed in String form 
 *   Input : String, String
 *   Output: String          
**)
fun mulPoly(x : string, y: string) = 
    let
        val p = parsePoly(x)
        val q = parsePoly(y)
    in
        polyToString(purgeZeroes(mulPolyByTerm(p,q)))
    end;

(**  
 *   Divide to polynomilas expressed in String form 
 *   Input : String, String
 *   Output: String          
**)
fun divPoly(x: string, y:string)=
    let 
        val p = parsePoly(x)
        val q = parsePoly(y)
        val r = divPolyByTerm(p,q)
    in
        if(( not( polyEquals(# rem r, parsePoly("0.0")))) andalso ( not( polyEquals(# quot r, parsePoly("1.0"))))) then
            polyToString( #quot r) ^ "+ ( (" ^ polyToString( #rem r) ^ ")/(" ^ polyToString(q) ^") )"
        else if((( not( polyEquals(# rem r, parsePoly("0.0")))))) then
            "1.0+ ( (" ^ polyToString( #rem r) ^ ")/(" ^ polyToString(q) ^") )"
        else if(( not( polyEquals(# quot r, parsePoly("1.0"))))) then
            polyToString( #quot r) 
        else 
            "1.0"
    end;

(**  
 *   Integrate a polynomial expressed in String form 
 *   Input : String
 *   Output: String          
**)
fun integratePoly(x: string) = polyToString(purgeZeroes(integratePolyByTerm(parsePoly x)));

(**  
 *   Get power of a polynomilal expressed in String form 
 *   Input : String, int
 *   Output: String          
**)
fun powPoly(x:string, i : int) = powPolyByTerm( parsePoly(x), i);

(**  
 *   Convert a list of roots into a list of factors in Term form
 *   Input : real list
 *   Output: Poly list        
**)
fun rootListToPoly(p : real list)=
    if(null p) then []
    else if(hd(p) < 0.0) then 
        parsePoly("x+"^Real.toString(Real.abs(hd(p))))::rootListToPoly(tl p)
    else
        parsePoly("x-"^Real.toString(Real.abs(hd(p))))::rootListToPoly(tl p);

(**  
 *   Convert a list of roots into a list of factors in String form
 *   Input : real list
 *   Output: String        
**)
fun rootListToString(p : real list)=
    if(null p) then ""
    else if(hd(p) < 0.0) then 
        "(x+"^Real.toString(Real.abs(hd(p)))^")"^rootListToString(tl p)
    else
        "(x-"^Real.toString(Real.abs(hd(p)))^")"^rootListToString(tl p);

(**  
 *   Factorize a polynomial to the string form of its factors
 *   Input : Poly, real list
 *   Output: String
**)
fun factorizeToString(p : Poly, fac : real list) = 
    if null p then ""
    else if null fac then "("^polyToString(p)^")"
    else 
        let 
            val an = getCoeffOf(getMaxPower(p), p)
            fun divMax(x : real) = x/an 
            val negRoots = map Real.~ fac
            val allFac = fac @ negRoots
            val potRoots = map divMax allFac
            val roots = substAndCheck(p,potRoots)
        in
            rootListToString(roots)
        end;


(**  
 *   Factorize a polynomial to the string form of its factors
 *   Input : Poly, real list
 *   Output: Poly list
**)
fun factorizeToPoly(p : Poly, fac : real list) = 
     if null p then []
    else if null fac then [p]
    else 
        let 
            val an = getCoeffOf(getMaxPower(p), p)
            fun divMax(x : real) = x/an 
            val negRoots = map Real.~ fac
            val allFac = fac @ negRoots
            val potRoots = map divMax allFac
            val roots = substAndCheck(p, potRoots)
        in
            rootListToPoly(roots)
        end;


(**  
 *   Factorize a polynomial to the string form of its factors
 *   Input : Poly
 *   Output: String
**)
fun factorizePolyByTermToString(k : Poly) = 
    let
        val p = makeNomial(k)
        val commonConst = termDivide(hd(k), hd(p))
        val constFactor = parsePoly(Real.toString(#coeff commonConst))
        val p1 = removeVar(p)
        val a0 = if null (tl p1) then getConstant(p)
                 else getConstant(hd(tl(p1)))
        val fac = getFactors(Real.abs(a0))
    in 
        if (null k) then ""
        else if( not (null(tl p1)) andalso not(Real.==( #coeff(commonConst), 1.0) ))  then
            "(" ^ polyToString(constFactor) ^ ")(" ^ polyToString(hd(p1)) ^ ")" ^ factorizeToString( hd(tl(p1)), fac)
        else if( not (null(tl p1) )) then
            "(" ^ polyToString(hd(p1)) ^ ")" ^ factorizeToString( hd(tl(p1)), fac)
        else if(not(Real.==( #coeff(commonConst), 1.0) )) then
            "(" ^ polyToString(constFactor)^")"^factorizeToString(hd(p1), fac)
        else
            factorizeToString( hd(p1), fac)
    end;    

(**  
 *   Factorize a polynomial to the Poly form of its factors
 *   Input : Poly
 *   Output: Poly list
**)
fun factorizePolyByTermToPoly(k : Poly) =
    let
        val p = makeNomial(k)
        val commonConst = termDivide(hd(k), hd(p))
        val constFactor = parsePoly(Real.toString( #coeff commonConst))
        val p1 = removeVar(p)
        val a0 = if null (tl p1) then getConstant(p)
                 else getConstant(hd(tl p1))
        val fac = getFactors(Real.abs(a0))
    in
        if( not (null(tl p1)) andalso not(Real.==( #coeff(commonConst), 1.0) ))  then
            constFactor::hd(p1)::factorizeToPoly( hd(tl(p1)), fac)
        else if( not (null(tl p1) )) then
            hd(p1)::factorizeToPoly( hd(tl(p1)), fac)
        else if(not(Real.==( #coeff(commonConst), 1.0) )) then
            constFactor::factorizeToPoly(hd(p1), fac)
        else
            factorizeToPoly( hd(p1), fac)
    end;


(**  
 *   Factorize a polynomial to the string form of its factors
 *   Input : String
 *   Output: String
**)
fun factorizePoly( k : string) = factorizePolyByTermToString( parsePoly( k));

(**  
 *   Convert a list of Polys to String
 *   Input : String, String
 *   Output: String          
**)
fun polyListToString(p : Poly list) =
    if(null p) then ""
    else "("^polyToString(hd p)^")"^polyListToString(tl(p));

fun removeRepeat(p : Poly list) = 
    if(null p) then []
    else if(polyMember( hd(p), tl(p))) then removeRepeat(tl p)
    else hd(p) :: removeRepeat(tl p);

(**  
 *   Get common factors of two polynomials 
 *   Input : String, String
 *   Output: String          
**)
fun getCommonFactors(k : string, l : string) =
    let
        val p = parsePoly(k)
        val q = parsePoly(l)
        val fac1 = factorizePolyByTermToPoly(p)
        val fac2 = factorizePolyByTermToPoly(q)
        val comm = []
        val f1 = getCommonMembers(fac1, fac2, comm)
        val f2 = getCommonMembers(fac2, fac1, comm)
        val ret = f1 @ f2
    in
        polyListToString(removeRepeat(ret))
    end;



