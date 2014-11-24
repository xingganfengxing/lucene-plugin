package tests;

import com.caucho.junit.ConfigurationBaratine;
import com.caucho.junit.ConfigurationBaratine.Log;
import com.caucho.junit.RunnerBaratine;
import com.caucho.lucene.LuceneService;
import com.caucho.lucene.LuceneServiceClient;
import io.baratine.core.Lookup;
import io.baratine.core.ServiceManager;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

@RunWith(RunnerBaratine.class)
@ConfigurationBaratine(services = {LuceneService.class},
                       testTime = 0,
                       logs = {@Log(name = "com.caucho",
                                    level = "FINER")})
public class T000 extends BaseTest
{
  @Test(timeout = 2000)
  public void testText() throws InterruptedException, IOException
  {
    test("test-00.txt");
  }

  @Test(timeout = 5000)
  public void testPdf() throws InterruptedException, IOException
  {
    test("test-00.pdf");
  }

  @Test(timeout = 5000)
  public void testWord() throws InterruptedException, IOException
  {
    test("test-00.docx");
  }

  @Before
  public void setUp()
  {
    _lucene.clear(v -> {
    });
  }

  private void test(String fileName) throws InterruptedException, IOException
  {
    List<String> result = uploadAndSearch(fileName, "Lorem");
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(makeBfsPath(fileName), result.get(0));
  }
}

