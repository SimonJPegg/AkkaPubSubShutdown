# What is this nonsense?

An actor system that implements the 'Do A Bunch Of Work Then Die' 
pattern using a PubSub model for message propagation.  

Actors lower down the chain request work from those further up via 
an Event bus.  Actors at the top of the chain then publish 'work' via a 
distinct Event bus.  The approach uses a pool of actors at each stage 
and requires each pool of actors to share a mailbox. A 'reaper' actor 
tracks the amount of work completed by the system and terminates 
(the system) once it deems the work to be complete.

### couldn't this be done with the `ask` pattern?

Yep, totally could. But this approach decouples actors at each processing 
stage, avoids having to set a timeout for waiting for responses, and seemed 
like a bit of fun to try.



