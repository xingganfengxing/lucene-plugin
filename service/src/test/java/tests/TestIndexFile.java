package tests;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import com.caucho.junit.ConfigurationBaratine;
import com.caucho.junit.RunnerBaratine;
import com.caucho.junit.ServiceTest;
import com.caucho.lucene.LuceneEntry;
import com.caucho.lucene.LuceneFacadeImpl;
import com.caucho.lucene.LuceneReaderImpl;
import com.caucho.lucene.LuceneWriterImpl;
import com.caucho.lucene.SearcherUpdateServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * title: test indexFile index
 */

@RunWith(RunnerBaratine.class)
@ServiceTest(LuceneWriterImpl.class)
@ServiceTest(LuceneReaderImpl.class)
@ServiceTest(LuceneFacadeImpl.class)
@ServiceTest(SearcherUpdateServiceImpl.class)
public class TestIndexFile extends Base
{
  @Test
  public void test()
    throws InterruptedException, IOException, ExecutionException
  {
    List<String> files = Arrays.asList("test-00.txt",
                                       "test-00.pdf",
                                       "test-00.docx");

    for (int i = 0; i < files.size(); i++) {
      String file = files.get(i);

      LuceneEntry[] result = uploadAndSearch(file, "Lorem");

      Arrays.sort(result,
                  (a, b) -> files.indexOf(Stream.of(a.getExternalId()
                                                     .split("/"))
                                                .reduce((c, d) -> d).get()) -
                            files.indexOf(Stream.of(b.getExternalId()
                                                     .split("/"))
                                                .reduce((c, d) -> d).get()));

      Assert.assertEquals(i + 1, result.length);
      for (int j = 0; j < result.length; j++) {
        Assert.assertEquals(makeBfsPath(files.get(j)),
                            result[j].getExternalId());
      }
    }
  }
}

