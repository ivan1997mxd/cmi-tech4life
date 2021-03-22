package com.estethapp.media.covid.device;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class PendingPermission
{
    private int Id;
    private boolean Completed;
    private boolean Accepted;
    private CountDownLatch Latch;

    public PendingPermission(int id)
    {
        Id = id;
        Latch = new CountDownLatch(1);
    }

    public PendingPermission(boolean result)
    {
        Id = 0;
        Accepted = result;
        Completed = true;
        Latch = new CountDownLatch(0);
    }

    public int getId()
    {
        return Id;
    }

    PermissionFuture getFuture()
    {
        return new PermissionFuture(this);
    }

    public void accept()
    {
        Accepted = true;
        Completed = true;
        Latch.countDown();
    }

    public void deny()
    {
        Accepted = false;
        Completed = true;
        Latch.countDown();
    }

    static class PermissionFuture implements Future<Boolean>
    {
        private final PendingPermission Permission;

        public PermissionFuture(PendingPermission permission)
        {
            Permission = permission;
        }

        @Override
        public boolean cancel(boolean b)
        {
            return false;
        }

        @Override
        public boolean isCancelled()
        {
            return false;
        }

        @Override
        public boolean isDone()
        {
            return Permission.Completed;
        }

        @Override
        public Boolean get()
        {
            try
            {
                Permission.Latch.await();
                return Permission.Accepted;
            }
            catch (InterruptedException ex)
            {
                return false;
            }
        }

        @Override
        public Boolean get(long l, TimeUnit timeUnit) throws InterruptedException
        {
            Permission.Latch.await(l, timeUnit);
            return Permission.Accepted;
        }
    }
}