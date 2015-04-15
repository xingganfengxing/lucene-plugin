package com.caucho.lucene;

import io.baratine.core.Journal;
import io.baratine.core.OnDestroy;
import io.baratine.core.OnLookup;
import io.baratine.core.Service;
import io.baratine.core.Startup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Service("pod://lucene/lucene")
@Journal
@Startup
public class LuceneManagerImpl
{
  private static Logger log
    = Logger.getLogger(LuceneManagerImpl.class.getName());

  private Map<String,LuceneIndexImpl> _map = new HashMap<>();

  private String _indexDirectory = "/tmp";

  public LuceneManagerImpl()
  {
  }

  public void setIndexDirectory(String indexDirectory)
  {
    _indexDirectory = indexDirectory;
  }

  @OnLookup
  public Object lookup(final String path) throws IOException
  {
    LuceneIndexImpl lucene = _map.get(path);

    if (lucene == null) {
      String address = path.substring(path.lastIndexOf("///") + 3);

      lucene = new LuceneIndexImpl(address, _indexDirectory);

      _map.put(path, lucene);
    }

    return lucene;
  }

  @OnDestroy
  public void destroy()
  {
    log.finer("destroying " + this);

    _map.values().forEach(l -> {try {l.destroy();} catch (Exception e) {}});
  }

  @Override
  public String toString()
  {
    return this.getClass().getSimpleName() + "[]";
  }
}