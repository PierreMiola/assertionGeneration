package test;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.util.ArrayList;
import java.util.List;


class TestListener extends RunListener {

    private List<Description> testRun;
    private List<Failure> testFails;

    TestListener() {
        this.testRun = new ArrayList<>();
        this.testFails = new ArrayList<>();
    }

    @Override
    public synchronized void testFinished(Description description) throws Exception {
        this.testRun.add(description);
    }

    @Override
    public synchronized void testFailure(Failure failure) throws Exception {
        this.testFails.add(failure);
    }

    @Override
    public synchronized void testAssumptionFailure(Failure failure) {
        //empty
    }

    List<Failure> getTestFails() {
        return testFails;
    }
    List<Description> getTestRun() {
        return testRun;
    }
}
