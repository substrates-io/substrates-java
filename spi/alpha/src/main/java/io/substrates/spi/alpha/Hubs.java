/*
 * Copyright © 2022 JINSPIRED B.V.
 */

package io.substrates.spi.alpha;

import io.humainary.substrates.Substrates;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.humainary.substrates.Substrates.*;
import static io.humainary.substrates.Substrates.Outlet.empty;

final class Hubs {

  private Hubs () {}

  static < E > Hub< E > of () {

    return
      new Memory<> (
        Environment.EMPTY
      );

  }

  static < E > Hub< E > of (
    final Environment environment
  ) {

    return
      new Memory<> (
        environment
      );

  }

  private static final class Memory< E >
    implements Hub< E > {

    @SuppressWarnings ( "rawtypes" )
    private static final AtomicReferenceFieldUpdater< Memory, Entry > U =
      AtomicReferenceFieldUpdater.newUpdater (
        Memory.class,
        Entry.class,
        "entries"
      );

    private final Environment environment;

    private volatile Entry< E > entries;

    public Memory (
      final Environment environment
    ) {

      this.environment =
        environment;

    }

    private static < E > Entry< E > of (
      final Membership< E > membership,
      final Entry< E > next
    ) {

      return
        new Entry<> (
          membership,
          next
        );

    }

    private static < E > Entry< E > scan (
      final Entry< E > initial
    ) {

      var next =
        initial;

      while (
        next != null &&
          next.membership == null
      ) {

        next =
          next.next;

      }

      return
        next;

    }


    public void publish (
      final Reference reference,
      final E value
    ) {

      final var head =
        entries;

      if ( head != null ) {

        dispatch (
          reference,
          value,
          head
        );

      }

    }

    public void publish (
      final Reference reference,
      final Supplier< ? extends E > supplier
    ) {

      final var head =
        entries;

      if ( head != null ) {

        dispatch (
          reference,
          supplier,
          head
        );

      }

    }

    @Override
    public Subscription subscribe (
      final Subscriber< E > subscriber
    ) {

      final var registration =
        new Membership<> (
          subscriber
        );

      //noinspection unchecked
      return
        U.updateAndGet (
          this,
          next ->
            of (
              registration,
              scan (
                next
              )
            )
        );

    }


    private void dispatch (
      final Reference reference,
      final E emittance,
      final Entry< E > head
    ) {

      assert head != null;

      var current =
        head;

      Entry< E > prev =
        null;

      Event< E > event = null;

      do {

        final var next =
          scan (
            current
          );

        if ( next != current ) {

          remove (
            prev,
            next,
            head
          );

          if ( next == null )
            return;

          current =
            next;

        }

        if ( event == null ) {

          event =
            new Event<> (
              reference,
              emittance
            );

        }

        current
          .accept (
            event
          );

        prev =
          current;

      } while (
        ( current = current.next ) != null
      );

    }

    private void dispatch (
      final Reference emitter,
      final Supplier< ? extends E > supplier,
      final Entry< E > head
    ) {

      assert head != null;

      var current =
        head;

      Entry< E > prev =
        null;

      Event< E > event = null;

      do {

        final var next =
          scan (
            current
          );

        if ( next != current ) {

          remove (
            prev,
            next,
            head
          );

          if ( next == null )
            return;

          current =
            next;

        }

        if ( event == null ) {

          event =
            new Event<> (
              emitter,
              supplier.get ()
            );

        }

        current
          .accept (
            event
          );

        prev =
          current;

      } while (
        ( current = current.next ) != null
      );

    }

    private void remove (
      final Entry< E > prev,
      final Entry< E > next,
      final Entry< E > head
    ) {

      if ( prev != null ) {

        prev.next =
          next;

      } else {

        U.compareAndSet (
          this,
          head,
          next
        );

      }

    }

    @Override
    public Substrates.Inlet< E > inlet (
      final Reference reference
    ) {

      return
        new Inlet<> (
          this,
          reference
        );

    }

    @Override
    public Environment environment () {

      return
        environment;

    }

  }


  static final class Entry< E >
    implements Outlet< E >,
               Subscription {

    volatile Membership< E > membership;

    Entry< E > next;

    Entry (
      final Membership< E > membership,
      final Entry< E > next
    ) {

      this.membership =
        membership;

      this.next =
        next;

    }


    @Override
    public void accept (
      final Substrates.Event< E > event
    ) {

      final var target =
        membership;

      if ( target != null ) {

        try {

          target.accept (
            event
          );

        } catch (
          final Throwable error
        ) {

          error.printStackTrace ();

          membership =
            null;

        }

      }

    }


    @Override
    public void cancel () {

      if ( membership != null ) {

        membership =
          null;

      } else {

        throw
          new IllegalStateException ();

      }

    }

  }


  private static final class Membership< E >
    implements Outlet< E > {

    private final Map< Reference, Outlet< E > > outlets =
      new ConcurrentHashMap<> ( 50 );

    private final Subscriber< E > delegate;

    Membership (
      final Subscriber< E > subscriber
    ) {

      delegate =
        subscriber;

    }

    @Override
    public void accept (
      final Substrates.Event< E > event
    ) {

      final Reference reference =
        event.emitter ();

      final var outlet =
        outlets.computeIfAbsent (
          reference,
          name ->
            newOutlet (
              reference
            )
        );

      if ( outlet != empty () ) {

        outlet.accept (
          event
        );

      }

    }

    private Outlet< E > newOutlet (
      final Reference reference
    ) {

      final var registrar =
        new Registration< E > ();

      delegate.accept (
        reference,
        registrar
      );

      return
        registrar.outlet;

    }


    private static final class Registration< E >
      implements Registrar< E > {

      Outlet< E > outlet =
        empty ();

      @Override
      public void register (
        final Outlet< E > outlet
      ) {

        this.outlet =
          outlet;

      }

    }

  }

  private record Event< T >(Reference emitter, T emittance)
    implements Substrates.Event< T > {

    @Override
    public < R > Event< R > map (
      final Function< ? super T, ? extends R > func
    ) {

      return
        new Event<> (
          emitter,
          func.apply (
            emittance
          )
        );

    }

  }

  private record Inlet< E >(
    Memory< ? super E > memory,
    Reference reference
  ) implements Substrates.Inlet< E > {


    @Override
    public void emit (
      final E event
    ) {

      memory.publish (
        reference,
        event
      );

    }

    @Override
    public void emit (
      final Supplier< ? extends E > supplier
    ) {

      memory.publish (
        reference,
        supplier
      );

    }

  }

}
