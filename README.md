# Constructoo: Delivery for Construction
In the current digital age, rapid growth in technology has made many companies rely on mobile apps. Many businesses find that mobile applications can provide better solutions and more efficient work processes at a fast-paced time of the day. Constructoo, an Android application, will assist the managers in the company by controlling the packages, determining the construction sites, and assigning the specified driver for this shipment. Moreover, monitoring shipments and knowing their status will also be present. It will also help vendors track the shipments and intimate them when the packages have been delivered.

## Functional Requirements
### Basic Features
1. The manager can create and maintain a list of building-sites, vendors, and drivers. Information recorded will include:
    * Construction sites: Name, address and manager
    * Drivers: Name and Email id
    * Vendors: Name, Email id and address
2. The manager can view the list of building sites, vendors, drivers, and packages and make changes to their lists. Separate columns for pending and completed packages provided.
3. The manager can add a new package, choose a vendor, and assign this package to a driver
4. After the initial details of a new driver (full name and email) have been added to the system by the manager, the driver can sign up to their account and complete their profile by adding new password and profile photo.
5. The diver is notified when they are allocated a new delivery job and should be able to see a list of the packages they need to deliver.
6. When a driver marks the package as delivered, the building site's admin receives a message notifying them of that delivery
### Advanced Features
* Firebase used for the backend database
* Vendor and Site manager developed as separate entities (both have their own portals)
* Dark Mode made
## Nonfunctional Requirements
* Ease of use: clients do not need any training to use the app.
* Security: The application will protect users’ passwords and private information.
* Network agnosticism: The app attempt to submitting data over wifi connections or cell networks
* Transactional data submissions: If a network connection is broken or data transfer is otherwise unsuccessful, the application can save all data locally and record a failed upload.

## Application workflow
![image](https://user-images.githubusercontent.com/26760537/165154870-01940608-007a-4858-96d5-de45cb399a43.png)

## Database overview
![image](https://user-images.githubusercontent.com/26760537/165155061-b121ceb1-03d1-4490-9ed3-99632fc8df4a.png)

## Screenshots
![image](https://user-images.githubusercontent.com/26760537/165155215-8316f924-3945-48de-b642-24a5b5286b13.png)


