/*
* Copyright 2012, CMM, University of Queensland.
*
* This file is part of Paul.
*
* Paul is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Paul is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Paul. If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.uq.cmm.paul.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.AbstractView;

/**
 * This view can be used to implement downloading of files from the 
 * ingestion queue area.  Note - you are likely to get better performance if
 * you use a native code webserver instead of using Tomcat for file downloads.
 * However, there's a good chance that this won't be significant.
 * 
 * @author scrawley
 */
public class FileView extends AbstractView {
    private static final Logger LOG = LoggerFactory.getLogger(FileView.class);

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model,
            HttpServletRequest request, HttpServletResponse response) 
                    throws IOException {
        File file = (File) model.get("file");
        String contentType = (String) model.get("contentType");
        try (FileInputStream fis = new FileInputStream(file)) {
            response.setContentType(contentType);
            long length = file.length();
            if (length <= Integer.MAX_VALUE) {
                response.setContentLength((int) length);
            }
            response.setStatus(HttpServletResponse.SC_OK);
            try (OutputStream os = response.getOutputStream()) {
                byte[] buffer = new byte[8192];
                int nosRead;
                while ((nosRead = fis.read(buffer)) > 0) {
                    os.write(buffer, 0, nosRead);
                }
            }
        } catch (FileNotFoundException ex) {
            LOG.info("Cannot access file: " + ex.getLocalizedMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

}
