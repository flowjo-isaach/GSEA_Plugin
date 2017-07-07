package main;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Created by Isaac on 7/2/2017.
 */
@RunWith(Arquillian.class)
public class Enricher_RequestTest {
    @Test
    public void prepare_request() throws Exception {
    }

    @Test
    public void send_request1() throws Exception {
    }

    @Test
    public void add_gene() throws Exception {
    }

    @Test
    public void checkURL() throws Exception {
    }

    @Test
    public void send_request() throws Exception {

    }

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addClass(Enricher_Request.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }


}