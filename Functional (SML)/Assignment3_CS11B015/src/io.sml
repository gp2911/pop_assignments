(* I/O functions *)

(* accept a user-defined poly *)
fun getPoly() = Option.getOpt(TextIO.inputLine(TextIO.stdIn), "0"); 
