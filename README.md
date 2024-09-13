
# Files Copier
For my job at Bungee Jumping in Kraków, I have created a **desktop app in Java**, designed to optimize the transfer of photos and videos of clients taken during the jump to them. 

Previously, we have developed a system of folders, which were synchronized with Google Drive and to which our clients received links. 
Each folder has a structure like:

## Folder Structure Overview

| **SUBFOLDER ID**   | **FOLDER ID**    | **6 RANDOM NUMBERS**  |
|--------------------|------------------|-----------------------|
| Number (0-500)     | Letter (A-Z)     | Random Unique Number  |



### Explanation:

- **SUBFOLDER ID**: A unique identifier assigned to each subfolder, ranging from numbers 0-500.
- **FOLDER ID**: A unique identifier, represented by a single letter of the alphabet (A-Z).
- **6 RANDOM NUMBERS**: A randomly generated 6-digit number used to ensure private access to the media files.
  
### Example: 
In s1 folder there are 500 subfolders with a structure like:
**123s456789**

The combination of the **SUBFOLDER ID**, **FOLDER ID**, and **6 RANDOM NUMBERS** creates a unique folder path to securely store and access media files for each client.

# Application logic



My program is designed to scan the files on SD-Cards: videos from Go-Pro cameras and photos form the digital camera.
All the previously scanned files are kept in a database and than, the newly taken photos/videos are not yet in a database, so they are selected as the files, that should be copied to the client's folder. 
To avoid checking iterating through thousands of files by every scan, I have implemented the heurystics, allowing to shorten the time complexity of scanning.



## App's interface. 
![image](https://github.com/user-attachments/assets/e0a582a6-20aa-4ac4-8ef3-3eb5945b503d)
- Left side is used to select paths to SD-Cards containing photos and videos as well as selecting clients' folders for the files to be copied to.
- Next, previous and empty buttons are designed for navigating through subfolders. "Empty" button is referring to the first empty subfolder, which should be the next client's subfolder.
- On the right side are buttons for scanning for new photos (Photos button), videos (Video button) and both (Photos & Video button). 
- Copy button is designed to copy selected files accordingly to the right folder. You can also view the subfolder's name on the button to ensure the correctness of the copying proccess. After performing each copy the next subfolder is chosen automatically.
- At the bottom there are two media panels for previewing videos (on the left) and photos (on the right). It also to check if the photos/videos were taking correctly
  (have the right length in case of videos, or the correct colour balance in regard to photos)
- Over each panel there's a Delete button allowing to delete the media files, that were taken by mistake or have any other problems with quality

## Additional options 
![image](https://github.com/user-attachments/assets/5557c7fc-3746-40eb-bb23-708c16c1424f)

- Skip current folder is a command designed to mark currently selected subfolder (Next client's folder) as not empty. It is useful in cases, where the client didn't perform the jump and no videos/photos
were taken, but the next subfolders were already assigned to other clients.
- Clear the files is a function, that adds all scanned files to the database and marks them as copied, so when the new files appear, only those will be viewed as copyable for clients. It is useful, when
 some photos were taken by accident, to check camera settings or the photos were simply of something other than clients.

# Using the app 

### Demonstration of app's workflow, simulating it's functioning at a real life situation

https://github.com/user-attachments/assets/0979996d-b825-41f2-895e-c7f1386d4419

# Resources and technologies used 

- Application's logic is written in Java, as it is easily integrated with interface and database technologies
- Interface was created using JavaFx library and Maven components
- Layout was made using fxml and styling in css
- For database integration I used SQLite

# Downloading the app
You can access the app in .exe format under following link:

https://drive.google.com/file/d/1a6zCQGZrQkl5pU4nSu81S64kZl4jy0QO/view?usp=drive_link
  
