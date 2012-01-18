/*
 *  Copyright (C) 2011 Daniel Heinrich <DannyNullZwo@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package anttasks;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

/**
 *
 * @author Daniel Heinrich <DannyNullZwo@gmail.com>
 */
public class ResourceCompiler extends Task
{
    private String dest;
    private final List<FileSet> filesets;

    public ResourceCompiler() {
        filesets = new LinkedList<FileSet>();
    }

    public void addFileset(FileSet set) {
        filesets.add(set);
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    @Override
    public void execute() throws BuildException {
        List<File> files = new LinkedList<File>();

        for (FileSet fs : filesets) {
            DirectoryScanner ds = fs.getDirectoryScanner(getProject());

            File dir = fs.getDir(getProject());
            for (String fileName : ds.getIncludedFiles())
                files.add(new File(dir, fileName));
        }

        
    }
}
