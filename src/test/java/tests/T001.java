package tests;

import com.caucho.junit.ConfigurationBaratine;
import com.caucho.junit.ConfigurationBaratine.Log;
import com.caucho.junit.RunnerBaratine;
import com.caucho.lucene.LuceneService;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RunWith(RunnerBaratine.class)
@ConfigurationBaratine(services = {LuceneService.class},
                       testTime = 0,
                       logs = {@Log(name = "com.caucho",
                                    level = "FINER")})
public class T001 extends BaseTest
{
  @Test(timeout = 2000)
  public void testDelete()
    throws InterruptedException, IOException, ExecutionException
  {
    test("test-00.txt");
  }

  @Before
  public void setUp()
  {
    _lucene.clear(v -> {
    });
  }

  private void test(String fileName)
    throws InterruptedException, IOException, ExecutionException
  {
    String[] result = uploadAndSearch(fileName, "Lorem");
    Assert.assertEquals(1, result.length);
    Assert.assertEquals(makeBfsPath(fileName), result[0]);

    delete(fileName);

    result = search("Lorem");

    Assert.assertEquals(0, result.length);
  }
}

